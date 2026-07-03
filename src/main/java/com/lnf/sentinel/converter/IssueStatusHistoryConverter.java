package com.lnf.sentinel.converter;

import com.lnf.dto.sentinel.IssueStatusHistoryDto;
import com.lnf.sentinel.model.IssueStatusHistory;
import com.lnf.sentinel.model.enums.IssueStatus;

public final class IssueStatusHistoryConverter {

    private IssueStatusHistoryConverter() {
    }

    public static IssueStatusHistoryDto toDto(IssueStatusHistory entity) {

        if (entity == null) {
            return null;
        }

        IssueStatusHistoryDto dto = new IssueStatusHistoryDto();

        dto.setIssueId(entity.getIssueId());
        dto.setNote(entity.getNotes());
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

        dto.setChangedBy(entity.getChangedBy());

        return dto;
    }


    public static IssueStatusHistory toEntity(IssueStatusHistoryDto dto, IssueStatusHistory entity) {

        if (dto == null || entity == null) {
            return null;
        }

        entity.setIssueId(dto.getIssueId());
        entity.setNotes(dto.getNote());

        if (dto.getFromStatus() != null) {
            entity.setFromStatus(IssueStatus.valueOf(dto.getFromStatus()));
        }

        if (dto.getToStatus() != null) {
            entity.setToStatus(IssueStatus.valueOf(dto.getToStatus()));
        }

        entity.setChangedBy(dto.getChangedBy());
        return entity;
    }



}
