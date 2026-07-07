package com.lnf.sentinel.converter;

import com.lnf.dto.sentinel.IssueCommentDto;
import com.lnf.sentinel.model.IssueComment;

public class IssueCommentConverter {

    private IssueCommentConverter() {
    }

    public static IssueCommentDto toTransportModel(IssueComment entity) {

        if (entity == null) return null;
        return IssueCommentDto.builder()
                .id(entity.getId())
                .issueId(entity.getIssueId())
                .userName(entity.getUserName())
                .userEmail(entity.getUserEmail())
                .body(entity.getBody())
                .build();
    }

    public static IssueComment toEntityModel(IssueComment entity, IssueCommentDto transport) {
        if (entity == null || transport == null) return null;
        entity.setIssueId(transport.getIssueId());
        entity.setUserName(transport.getUserName());
        entity.setBody(transport.getBody());
        entity.setUserEmail(transport.getUserEmail());
        return entity;
    }

}
