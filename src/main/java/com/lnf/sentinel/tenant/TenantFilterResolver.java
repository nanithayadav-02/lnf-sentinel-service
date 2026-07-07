package com.lnf.sentinel.tenant;

import com.lnf.tenant.core.context.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TenantFilterResolver {

    @Value("${lnf.tenant.enabled}")
    private boolean tenantEnabled;
    @Value("${lnf.tenant.databaseName}")
    private String databaseName;

    public String resolvePrefix() {
        if (!tenantEnabled) {
            return "";
        }
        String tenantContext = TenantContext.getCurrentTenant();
        if (tenantContext == null || tenantContext.isBlank()) {
            return "";
        }
        log.debug("logging the resolvePrefix---> {}", extractTenantId(tenantContext) + "/");
        return extractTenantId(tenantContext) + "/";
    }

    private String extractTenantId(String tenantContext) {
        // TenantContext format: "{tenantId}-{databaseName}"
        // e.g., "lnfpeople-mt-file" → tenantId = "lnfpeople-mt"
        if (databaseName != null && tenantContext.endsWith("-" + databaseName)) {
            return tenantContext.substring(0, tenantContext.length() - databaseName.length() - 1);
        }
        // TenantFilter falls back to "<tenant>-null" when it cannot resolve the service
        // database name (e.g. sentinel service has no JPA datasource).
        if (tenantContext.endsWith("-null")) {
            return tenantContext.substring(0, tenantContext.length() - 5);
        }

        log.debug("logging the Tenant-Context--> {}", tenantContext);
        return tenantContext;
    }
}
