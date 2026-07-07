package com.lnf.sentinel.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenUtil {

    public Map<String, Object> extractJwtClaims() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            return jwtAuthenticationToken.getToken().getClaims();
        }

        log.warn("Authentication is not JwtAuthenticationToken: {}",
                authentication != null ? authentication.getClass().getName() : "null");

        return Collections.emptyMap();
    }

    public String getUserEmail() {
        return Optional.ofNullable(extractJwtClaims())
                .map(claims -> (String) claims.get("email"))
                .orElse(null);
    }

    public String getUserName() {
        return Optional.ofNullable(extractJwtClaims())
                .map(claims -> (String) claims.get("name"))
                .orElse(null);
    }

}
