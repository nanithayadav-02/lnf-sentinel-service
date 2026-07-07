package com.lnf.sentinel.converter;

import com.lnf.dto.sentinel.IssueDto;
import com.lnf.sentinel.model.Issue;
import com.lnf.sentinel.model.enums.*;
import org.apache.commons.lang3.StringUtils;

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
                .title(entity.getTitle())
                .description(entity.getDescription())
                .rootCause(entity.getRootCause())
                .tenantName(entity.getTenantName())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .severity(entity.getSeverity() != null ? entity.getSeverity().name() : null)
                .priority(entity.getPriority() != null ? entity.getPriority().name() : null)
                .category(entity.getCategory() != null ? entity.getCategory().name() : null)
                .environment(entity.getEnvironment() != null ? entity.getEnvironment().name() : null)
                .assigneeUserName(entity.getAssigneeUserName())
                .assigneeEmail(entity.getAssigneeEmail())
                .affectedService(entity.getAffectedService())
                .resolution(
                        entity.getResolution() != null
                                ? entity.getResolution()
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
        issue.setTitle(transport.getTitle());
        issue.setDescription(transport.getDescription());
        issue.setRootCause(transport.getRootCause());
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
        issue.setAssigneeUserName(transport.getAssigneeUserName());
        issue.setAssigneeEmail(transport.getAssigneeEmail());
        issue.setAffectedService(transport.getAffectedService());

        if (StringUtils.isNotBlank(transport.getResolution())) {
            issue.setResolution(transport.getResolution());
            issue.setResolvedAt(transport.getResolvedAt());
        }

        issue.setDetectedAt(transport.getDetectedAt());
        issue.setSlaDueAt(transport.getSlaDueAt());

        return issue;
    }

}