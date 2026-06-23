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
                .body(entity.getBody())
                .internal(entity.isInternal())
                .build();
    }

    public static IssueComment toEntityModel(IssueComment entity, IssueCommentDto transport) {
        if (entity == null || transport == null) return null;
        entity.setIssueId(transport.getIssueId());
        entity.setBody(transport.getBody());
        entity.setInternal(transport.isInternal());
        return entity;
    }

}
