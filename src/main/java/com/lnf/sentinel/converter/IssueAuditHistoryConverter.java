package com.lnf.sentinel.converter;

import com.lnf.dto.sentinel.IssueAuditHistoryDto;
import com.lnf.sentinel.model.Issue;
import com.lnf.sentinel.model.IssueAuditHistory;

public final class IssueAuditHistoryConverter {

    private IssueAuditHistoryConverter() {
    }

    public static IssueAuditHistoryDto toDto(IssueAuditHistory entity) {

        if (entity == null) {
            return null;
        }

        IssueAuditHistoryDto dto = new IssueAuditHistoryDto();

        dto.setId(entity.getId());
        dto.setComment(entity.getComment());
        dto.setIssueCode(entity.getIssueCode());

        if (entity.getIssue() != null) {
            dto.setIssueId(entity.getIssue().getId());
        }
        return dto;
    }

    public static IssueAuditHistory toEntity(IssueAuditHistoryDto dto, IssueAuditHistory entity) {

        if (dto == null || entity == null) {
            return null;
        }

        entity.setComment(dto.getComment());
        entity.setIssueCode(dto.getIssueCode());

        if (dto.getIssueId() != null) {
            Issue issue = new Issue();
            issue.setId(dto.getIssueId());
            entity.setIssue(issue);
        }
        return entity;
    }

}
