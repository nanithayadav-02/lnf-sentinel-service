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

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
@EnableWebSecurity
@Profile({"prod", "dev"})
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired(required = false)
    private JwtIssuerAuthenticationManagerResolver multiTenantJwtResolver;
    private String ACTUATOR_HEALTH_ENDPOINT_PATTERN;
    private String ACTUATOR_INFO_ENDPOINT_PATTERN;
    private String SERVICE_HEALTH_CHECK;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> {
                    // Publicly accessible endpoints
                    auth.requestMatchers(ACTUATOR_HEALTH_ENDPOINT_PATTERN,
                            ACTUATOR_INFO_ENDPOINT_PATTERN, SERVICE_HEALTH_CHECK,
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html").permitAll();

                    auth.anyRequest().authenticated();
                })
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> {
                    if (multiTenantJwtResolver != null) {
                        oauth2.authenticationManagerResolver(multiTenantJwtResolver);
                    } else {
                        oauth2.jwt(Customizer.withDefaults());
                    }
                });

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
        return token -> {
            try {
                String payload = new String(java.util.Base64.getUrlDecoder().decode(token.split("\\.")[1]));
                String issuer = new ObjectMapper().readTree(payload).get("iss").asText();
                return JwtDecoders.fromIssuerLocation(issuer).decode(token);
            } catch (Exception e) {
                throw new JwtException("Unable to extract issuer from token", e);
            }
        };
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
