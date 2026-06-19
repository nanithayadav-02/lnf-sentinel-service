package com.lnf.sentinel.dto;

import com.lnf.sentinel.domain.enums.SupportTier;
import com.lnf.sentinel.domain.enums.TenantStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTenantRequest(
        @NotBlank @Size(max = 40) String tenantCode,
        @NotBlank @Size(max = 160) String name,
        TenantStatus status,
        SupportTier supportTier,
        @Size(max = 40) String region,
        @Size(max = 255) String productionUrl,
        @Size(max = 160) String primaryContactName,
        @Email @Size(max = 160) String primaryContactEmail
) {}
