package com.lnf.sentinel.converter;

import com.lnf.dto.sentinel.IssueDto;
import com.lnf.sentinel.model.Issue;
import com.lnf.sentinel.model.enums.*;

import java.util.UUID;

public class IssueConverter {

    private IssueConverter() {
    }

    public static IssueDto toTransportModel(Issue entity) {

        if (entity == null) {
            return null;
        }

        return IssueDto.builder()
                .id(entity.getId())
                .issueKey(entity.getIssueKey())
                .summary(entity.getSummary())
                .description(entity.getDescription())
                .tenantCode(entity.getTenantCode())
                .tenantName(entity.getTenantName())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .severity(entity.getSeverity() != null ? entity.getSeverity().name() : null)
                .priority(entity.getPriority() != null ? entity.getPriority().name() : null)
                .category(entity.getCategory() != null ? entity.getCategory().name() : null)
                .environment(entity.getEnvironment() != null ? entity.getEnvironment().name() : null)
                .assigneeId(entity.getAssigneeId())
                .assignee(entity.getAssignee())
                .reportedBy(entity.getReportedBy())
                .affectedService(entity.getAffectedService())
                .resolution(
                        entity.getResolution() != null
                                ? entity.getResolution().name()
                                : null
                )
                .detectedAt(entity.getDetectedAt())
                .slaDueAt(entity.getSlaDueAt())
                .resolvedAt(entity.getResolvedAt())
                .slaBreached(entity.isSlaBreached())
                .build();
    }

    public static Issue toEntityModel(IssueDto transport, Issue issue) {

        if (transport == null || issue == null) {
            return null;
        }

        issue.setIssueKey(transport.getIssueKey());
        issue.setSummary(transport.getSummary());
        issue.setDescription(transport.getDescription());

        if (transport.getTenantCode() != null) {
            issue.setTenantCode(String.valueOf(transport.getTenantCode()));
        }
        issue.setTenantName(transport.getTenantName());

        if (transport.getStatus() != null) {
            issue.setStatus(IssueStatus.valueOf(transport.getStatus()));
        }

        if (transport.getSeverity() != null) {
            issue.setSeverity(Severity.valueOf(transport.getSeverity()));
        }
        if (transport.getPriority() != null) {
            issue.setPriority(Priority.valueOf(transport.getPriority()));
        }
        if (transport.getCategory() != null) {
            issue.setCategory(Category.valueOf(transport.getCategory()));
        }
        if (transport.getEnvironment() != null) {
            issue.setEnvironment(Environment.valueOf(transport.getEnvironment()));
        }
        issue.setAssigneeId(transport.getAssigneeId());
        issue.setAssignee(transport.getAssignee());
        issue.setReportedBy(transport.getReportedBy());
        issue.setAffectedService(transport.getAffectedService());

        if (transport.getResolution() != null) {
            issue.setResolution(Resolution.valueOf(transport.getResolution()));
        }

        issue.setDetectedAt(transport.getDetectedAt());
        issue.setSlaDueAt(transport.getSlaDueAt());
        issue.setResolvedAt(transport.getResolvedAt());

        return issue;
    }
}