package com.lnf.sentinel.dto;

import com.lnf.sentinel.domain.Tenant;
import com.lnf.sentinel.domain.enums.SupportTier;
import com.lnf.sentinel.domain.enums.TenantStatus;

import java.time.OffsetDateTime;

public record TenantResponse(
        Long id,
        String tenantCode,
        String name,
        TenantStatus status,
        SupportTier supportTier,
        String region,
        String productionUrl,
        String primaryContactName,
        String primaryContactEmail,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static TenantResponse from(Tenant t) {
        return new TenantResponse(t.getId(), t.getTenantCode(), t.getName(), t.getStatus(),
                t.getSupportTier(), t.getRegion(), t.getProductionUrl(),
                t.getPrimaryContactName(), t.getPrimaryContactEmail(),
                t.getCreatedAt(), t.getUpdatedAt());
    }
}
