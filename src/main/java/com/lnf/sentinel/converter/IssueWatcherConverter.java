package com.lnf.sentinel.converter;

import com.lnf.dto.sentinel.IssueStatusHistoryDto;
import com.lnf.dto.sentinel.IssueWatcherDto;
import com.lnf.sentinel.domain.IssueStatusHistory;
import com.lnf.sentinel.domain.IssueWatcher;

public final class IssueWatcherConverter {
    private  IssueWatcherConverter(){

    }
    public static IssueWatcherDto toDto(IssueWatcher entity){
        if(entity == null){
            return  null;
        }
        IssueWatcherDto dto = new IssueWatcherDto();
        dto.setId(entity.getId());
        dto.setIssueId(entity.getIssueId());
        dto.setUserId(entity.getUserId());
        return dto;
    }

    public  static  IssueWatcher toEntity(IssueWatcherDto dto , IssueWatcher entity){
        entity.setId(dto.getId());
        entity.setIssueId(dto.getIssueId());
        entity.setUserId(dto.getUserId());

        return  entity;
    }
}
