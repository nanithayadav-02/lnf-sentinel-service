package com.lnf.sentinel.converter;

import com.lnf.dto.sentinel.IssueStatusHistoryDto;
import com.lnf.sentinel.model.IssueStatusHistory;

public final class IssueStatusHistoryConverter {

    private IssueStatusHistoryConverter() {
    }


    public static IssueStatusHistoryDto toDto(IssueStatusHistory entity) {

        if (entity == null) {
            return null;
        }

        IssueStatusHistoryDto dto = new IssueStatusHistoryDto();

        dto.setIssueId(entity.getIssueId());
        dto.setChangedBy(entity.getChangedBy());
        dto.setNote(entity.getNote());
        dto.setFromStatus(
                entity.getFromStatus() != null
                        ? entity.getFromStatus().name()
                        : null
        );

        dto.setToStatus(
                entity.getToStatus() != null
                        ? entity.getToStatus().name()
                        : null
        );

        return dto;
    }


    public static IssueStatusHistory toEntity(
            IssueStatusHistoryDto dto,
            IssueStatusHistory entity
    ) {

        if (dto == null || entity == null) {
            return null;
        }

        entity.setIssueId(dto.getIssueId());
        entity.setChangedBy(dto.getChangedBy());
        entity.setNote(dto.getNote());
        dto.setFromStatus(
                entity.getFromStatus() != null
                        ? entity.getFromStatus().name()
                        : null
        );

        dto.setToStatus(
                entity.getToStatus() != null
                        ? entity.getToStatus().name()
                        : null
        );

        return entity;
    }
}