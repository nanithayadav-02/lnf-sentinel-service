package com.lnf.sentinel.converter;

import com.lnf.dto.sentinel.IssueWatcherDto;
import com.lnf.sentinel.model.IssueWatcher;

public final class IssueWatcherConverter {
    private IssueWatcherConverter() {

    }

    public static IssueWatcherDto toTransportModel(IssueWatcher entity) {
        if (entity == null) {
            return null;
        }
        IssueWatcherDto dto = new IssueWatcherDto();
        dto.setId(entity.getId());
        dto.setIssueId(entity.getIssueId());
        dto.setUserEmail(entity.getUserEmail());
        dto.setUserName(entity.getUserName());
        return dto;
    }

    public static IssueWatcher toEntityModel(IssueWatcherDto dto, IssueWatcher entity) {

        if (entity == null || dto == null) {
            return null;
        }
        entity.setId(dto.getId());
        entity.setIssueId(dto.getIssueId());
        entity.setUserName(dto.getUserName());
        entity.setUserEmail(dto.getUserEmail());
        return entity;
    }

}
