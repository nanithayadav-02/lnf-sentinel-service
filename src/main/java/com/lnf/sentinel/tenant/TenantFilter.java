package com.lnf.sentinel.tenant;

import com.lnf.sentinel.repository.TenantRepository;
import com.lnf.sentinel.model.Tenant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
public class TenantFilter extends OncePerRequestFilter {

    public static final String TENANT_HEADER = "X-Tenant-Id";
    public static final String TENANT_CLAIM = "tenant_id";

    private final TenantRepository tenantRepository;

    public TenantFilter(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        try {
            UUID tenantId = resolveTenant(request);

            if (tenantId != null) {
                TenantContext.setTenantId(tenantId);
            }

            chain.doFilter(request, response);

        } finally {
            TenantContext.clear();
        }
    }

    private UUID resolveTenant(HttpServletRequest request) {
        String header = request.getHeader(TENANT_HEADER);

        if (StringUtils.hasText(header)) {
            return resolveFromHeader(header.trim());
        }

        return resolveFromJwt();
    }

    private UUID resolveFromHeader(String header) {

        // Case 1: UUID sent directly
        try {
            UUID id = UUID.fromString(header);

            return tenantRepository.existsById(id) ? id : null;

        } catch (IllegalArgumentException ignored) {

        }

        // Case 2: tenant code
        return tenantRepository.findByTenantCode(header)
                .map(Tenant::getId)
                .orElse(null);
    }

    private UUID resolveFromJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {

            Object claim = jwt.getClaim(TENANT_CLAIM);

            if (claim != null) {
                try {
                    return UUID.fromString(String.valueOf(claim));
                } catch (IllegalArgumentException ignored) {
                    return null;
                }
            }
        }

        return null;
    }
}