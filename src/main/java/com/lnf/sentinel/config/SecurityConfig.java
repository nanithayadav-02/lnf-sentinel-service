/*
 *
 *  * Copyright © 2024 Lever And Fulcrum Solutions (hereinafter referred to as "LNF").
 *  * All rights reserved.
 *  *
 *  * This source code is the proprietary property of LNF
 *  *
 *  * Unauthorized copying, redistribution, or modification of this code,
 *  * via any medium, is strictly prohibited unless expressly authorized
 *  * in writing by LNF.
 *  *
 *  * This code is confidential and intended solely for the use of LNF
 *  * and its authorized personnel.
 *
 */

package com.lnf.sentinel.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSecurity
@Profile({"prod", "dev"})
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}")
    private String issuerUri;

    @Value("${lnf.tenant.enabled:false}")
    private boolean tenantEnabled;

    @Value("${lnf.tenant.auth.base-url:}")
    private String keycloakBaseUrl;

    @Value("${lnf.tenant.auth.internal-url:#{null}}")
    private String keycloakInternalUrl;

    private final Map<String, JwtDecoder> decoderCache = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health/**", "/actuator/info", "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/lnf-attendance/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults()));

        http.addFilterBefore(
                (servletRequest, servletResponse, chain) -> {
                    if (servletRequest instanceof HttpServletRequest request) {
                        String path = request.getServletPath();
                        if (path.startsWith("/v3/api-docs") ||
                                path.startsWith("/swagger-ui") ||
                                path.equals("/swagger-ui.html")) {
                            chain.doFilter(servletRequest, servletResponse);
                            return;
                        }
                    }
                    chain.doFilter(servletRequest, servletResponse);
                },
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        if (!tenantEnabled) {
            return token -> {
                try {
                    return NimbusJwtDecoder.withJwkSetUri(
                            issuerUri + "/protocol/openid-connect/certs").build().decode(token);
                } catch (Exception e) {
                    throw new InvalidBearerTokenException("JWT decode failed: " + e.getMessage());
                }
            };
        }
        return token -> {
            try {
                String issuer = extractIssuer(token);
                return decoderCache.computeIfAbsent(issuer, this::buildDecoder).decode(token);
            } catch (InvalidBearerTokenException e) {
                throw e;
            } catch (Exception e) {
                throw new InvalidBearerTokenException("Unable to extract issuer from token");
            }
        };
    }

    private String extractIssuer(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) throw new InvalidBearerTokenException("Invalid JWT format");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            JsonNode node = objectMapper.readTree(payload);
            if (!node.has("iss")) throw new InvalidBearerTokenException("Missing iss claim");
            return node.get("iss").asText();
        } catch (InvalidBearerTokenException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidBearerTokenException("Cannot parse JWT: " + e.getMessage());
        }
    }

    private JwtDecoder buildDecoder(String issuer) {
        String realmPath = issuer.substring(keycloakBaseUrl.length());
        String baseForJwks = (keycloakInternalUrl != null && !keycloakInternalUrl.isBlank())
                ? keycloakInternalUrl : keycloakBaseUrl;
        String jwkSetUri = baseForJwks + realmPath + "/protocol/openid-connect/certs";
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
        return decoder;
    }

    @Bean
    SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(sessionRegistry());
    }

    @Bean
    SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

}
