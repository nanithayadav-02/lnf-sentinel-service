package com.lnf.sentinel.tenant;

import java.util.UUID;

/**
 * Holds the tenant pinned to the current request thread.
 * <p>
 * A {@code null} tenant id means the caller is internal staff running
 * cross-tenant (sees everything). When a tenant id is present, every issue
 * query must be scoped to it.
 * <p>
 * The {@link TenantFilter} sets this at the start of a request and MUST clear
 * it in a {@code finally} block so thread-pool reuse never leaks tenant state.
 */
public final class TenantContext {

    private static final ThreadLocal<UUID> CURRENT = new ThreadLocal<>();

    private TenantContext() {}

    public static void setTenantId(UUID tenantId) {
        CURRENT.set(tenantId);
    }

    /**
     * @return the pinned tenant id, or {@code null} for cross-tenant (internal) access.
     */
    public static UUID getTenantId() {
        return CURRENT.get();
    }

    public static boolean isSet() {
        return CURRENT.get() != null;
    }

    public static void clear() {
        CURRENT.remove();
    }
}
