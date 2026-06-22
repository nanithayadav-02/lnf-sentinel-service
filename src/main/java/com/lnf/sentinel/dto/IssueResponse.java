package com.lnf.sentinel.dto;

import com.lnf.sentinel.domain.Issue;
import com.lnf.sentinel.domain.enums.*;

import java.time.OffsetDateTime;

public record IssueResponse(
        Long id,
        String issueKey,
        Long tenantId,
        String title,
        String description,
        Severity severity,
        Priority priority,
        IssueStatus status,
        Category category,
        Environment environment,
        String affectedService,
        Long reportedBy,
        Long assigneeId,
        Resolution resolution,
        String rootCause,
        String fixVersion,
        OffsetDateTime detectedAt,
        OffsetDateTime slaDueAt,
        OffsetDateTime resolvedAt,
        boolean slaBreached,
        OffsetDateTime createdAt,

        OffsetDateTime updatedAt
) {public static IssueResponse from(Issue i) {
        return new IssueResponse(
                i.getId(), i.getIssueKey(), i.getTenantId(), i.getTitle(), i.getDescription(),
                i.getSeverity(), i.getPriority(), i.getStatus(), i.getCategory(), i.getEnvironment(),
                i.getAffectedService(), i.getReportedBy(), i.getAssigneeId(),
                i.getResolution(), i.getRootCause(), i.getFixVersion(),
                i.getDetectedAt(), i.getSlaDueAt(), i.getResolvedAt(), i.isSlaBreached(),
                i.getCreatedAt(), i.getUpdatedAt());
    }
}
