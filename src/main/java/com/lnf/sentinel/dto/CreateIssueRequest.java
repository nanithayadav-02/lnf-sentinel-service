package com.lnf.sentinel.dto;

import com.lnf.sentinel.domain.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

/**
 * Create an issue. When the request is made cross-tenant (no tenant pinned),
 * {@code tenantId} is required; when a tenant is pinned it is taken from context.
 */
public record CreateIssueRequest(
        @NotBlank @Size(max = 240) String title,
        String description,
        Long tenantId,
        Severity severity,
        Priority priority,
        Category category,
        Environment environment,
        @Size(max = 120) String affectedService,
        Long reportedBy,
        Long assigneeId,
        OffsetDateTime detectedAt
) {}
