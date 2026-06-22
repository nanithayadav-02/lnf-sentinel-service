package com.lnf.sentinel.converter;


import com.lnf.dto.sentinel.IssueDto;
import com.lnf.sentinel.domain.Issue;
import com.lnf.sentinel.domain.enums.*;

public class IssueConverter {

    private IssueConverter() {
    }

    public static IssueDto toTransportModel(Issue entity){
        if(entity==null){
            return null;
        }
        return IssueDto.builder()
                .issueKey(entity.getIssueKey())
                .environment(String.valueOf(entity.getEnvironment()))
                .fixVersion(entity.getFixVersion())
                .priority(String.valueOf(entity.getPriority()))
                .detectedAt(entity.getDetectedAt())
                .reportedBy(entity.getReportedBy())
                .resolvedAt(entity.getResolvedAt())
                .resolution(String.valueOf(entity.getResolution()))
                .severity(String.valueOf(entity.getSeverity()))
                .title(entity.getTitle())
                .slaDueAt(entity.getSlaDueAt())
                .status(String.valueOf(entity.getStatus()))
                .description(entity.getDescription())
                .tenantId(entity.getTenantId())
                .affectedService(entity.getAffectedService())
                .category(String.valueOf(entity.getCategory()))
                .assigneeId(entity.getAssigneeId())
                .rootCause(entity.getRootCause())
                .title(entity.getTitle())
                .build();
    }

    public static Issue toEntityModel(IssueDto dto,Issue entity){

        if(dto==null ||entity==null){
            return null;
        }

        entity.setCategory(Category.valueOf(dto.getCategory()));
        entity.setDescription(dto.getDescription());
        entity.setAssigneeId(dto.getAssigneeId());
        entity.setEnvironment(Environment.valueOf(dto.getEnvironment()));
        entity.setIssueKey(dto.getIssueKey());
        entity.setPriority(Priority.valueOf(dto.getPriority()));
        entity.setResolution(Resolution.valueOf(dto.getResolution()));
        entity.setDetectedAt(dto.getDetectedAt());
     entity.setFixVersion(dto.getFixVersion());
     entity.setReportedBy(dto.getReportedBy());
     entity.setAffectedService(dto.getAffectedService());
     entity.setResolvedAt(dto.getResolvedAt());
     entity.setRootCause(dto.getRootCause());
     entity.setSeverity(Severity.valueOf(dto.getSeverity()));
     entity.setStatus(IssueStatus.valueOf(dto.getStatus()));
     entity.setTitle(dto.getTitle());
     entity.setTenantId(dto.getTenantId());
     entity.setSlaDueAt(dto.getSlaDueAt());
       return entity;
    }

}
