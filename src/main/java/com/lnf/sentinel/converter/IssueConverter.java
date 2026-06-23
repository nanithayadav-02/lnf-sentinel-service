package com.lnf.sentinel.converter;


import com.lnf.dto.sentinel.IssueDto;
import com.lnf.sentinel.model.Issue;
import com.lnf.sentinel.model.enums.*;

public class IssueConverter {

    private IssueConverter() {
    }

    public static IssueDto toTransportModel(Issue entity) {

        if (entity == null) return null;

        return IssueDto.builder()
                .id(entity.getId())
                .summary(entity.getSummary())
                .description(entity.getDescription())
                .tenantCode(entity.getTenantCode())
                .tenantName(entity.getTenantName())
                .status(String.valueOf(entity.getStatus()))
                .severity(String.valueOf(entity.getSeverity()))
                .priority(String.valueOf(entity.getPriority()))
                .category(String.valueOf(entity.getCategory()))
                .environment(String.valueOf(entity.getEnvironment()))
                .assigneeId(entity.getAssigneeId())
                .assignee(entity.getAssignee())
                .affectedService(entity.getAffectedService())
                .resolution(String.valueOf(entity.getResolution()))
                .detectedAt(entity.getDetectedAt())
                .reportedBy(entity.getReportedBy())
                .slaDueAt(entity.getSlaDueAt())
                .resolvedAt(entity.getResolvedAt())
                .slaBreached(entity.isSlaBreached())
                .build();

    }

    public static Issue toEntityModel(IssueDto transport, Issue entity) {

        if (transport == null || entity == null) return null;

        Issue issue = new Issue();
        issue.setIssueKey(transport.getIssueKey());
        issue.setSummary(transport.getSummary());
        issue.setDescription(transport.getDescription());
        issue.setTenantCode(transport.getTenantCode());
        issue.setTenantName(transport.getTenantName());
        issue.setStatus(IssueStatus.valueOf(transport.getStatus()));
        issue.setSeverity(Severity.valueOf(transport.getSeverity()));
        issue.setPriority(Priority.valueOf(transport.getPriority()));
        issue.setCategory(Category.valueOf(transport.getCategory()));
        issue.setEnvironment(Environment.valueOf(transport.getEnvironment()));
        issue.setAssigneeId(transport.getAssigneeId());
        issue.setAssignee(transport.getAssignee());
        issue.setReportedBy(transport.getReportedBy());
        issue.setAffectedService(transport.getAffectedService());
        issue.setResolution(Resolution.valueOf(transport.getResolution()));
        issue.setDetectedAt(transport.getDetectedAt());
        issue.setSlaDueAt(transport.getSlaDueAt());
        issue.setResolvedAt(transport.getResolvedAt());
        issue.setId(transport.getId());
        return issue;

    }

}
