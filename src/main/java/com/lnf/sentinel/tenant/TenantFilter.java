package com.lnf.sentinel.tenant;

import com.lnf.sentinel.repository.TenantRepository;
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

/**
 * Resolves the current tenant per request and pins it into {@link TenantContext}.
 * <p>
 * Resolution order:
 * <ol>
 *   <li>{@code X-Tenant-Id} header (internal UI scoping) — numeric id or tenant code</li>
 *   <li>a {@code tenant_id} claim on the JWT (a tenant user)</li>
 * </ol>
 * Internal staff send neither and therefore run cross-tenant. An
 * <em>unresolvable</em> header pins a non-existent tenant id so queries match
 * nothing and cross-tenant existence is never leaked.
 */
@Component
@Order(1)
public class TenantFilter extends OncePerRequestFilter {

    public static final String TENANT_HEADER = "X-Tenant-Id";
    public static final String TENANT_CLAIM = "tenant_id";
    private static final Long UNRESOLVABLE = -1L;

    private final TenantRepository tenantRepository;

    public TenantFilter(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        try {
            Long tenantId = resolveTenant(request);
            if (tenantId != null) {
                TenantContext.setTenantId(tenantId);
            }
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private Long resolveTenant(HttpServletRequest request) {
        String header = request.getHeader(TENANT_HEADER);
        if (StringUtils.hasText(header)) {
            return resolveFromHeader(header.trim());
        }
        return resolveFromJwt();
    }

    private Long resolveFromHeader(String header) {
        if (header.chars().allMatch(Character::isDigit)) {
            Long id = Long.valueOf(header);
            return tenantRepository.existsById(id) ? id : UNRESOLVABLE;
        }
        return tenantRepository.findByTenantCode(header)
                .map(t -> t.getId())
                .orElse(UNRESOLVABLE);
    }

    private Long resolveFromJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            Object claim = jwt.getClaim(TENANT_CLAIM);
            if (claim != null) {
                try {
                    return Long.valueOf(String.valueOf(claim));
                } catch (NumberFormatException ignored) {
                    return UNRESOLVABLE;
                }
            }
        }
        return null;
    }
}
