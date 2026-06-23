package com.lnf.sentinel.converter;

import com.lnf.dto.sentinel.IssueStatusHistoryDto;
import com.lnf.dto.sentinel.IssueWatcherDto;
import com.lnf.sentinel.model.IssueStatusHistory;
import com.lnf.sentinel.model.IssueWatcher;

public final class IssueWatcherConverter {
    private  IssueWatcherConverter(){

    }
    public static IssueWatcherDto toTransportModel(IssueWatcher entity){
        if(entity == null){
            return  null;
        }
        IssueWatcherDto dto = new IssueWatcherDto();
        dto.setId(entity.getId());
        dto.setIssueId(entity.getIssueId());
        dto.setUserId(entity.getUserId());
        return dto;
    }

    public  static  IssueWatcher toEntityModel(IssueWatcherDto dto , IssueWatcher entity){
        entity.setId(dto.getId());
        entity.setIssueId(dto.getIssueId());
        entity.setUserId(dto.getUserId());

        return  entity;
    }
}
