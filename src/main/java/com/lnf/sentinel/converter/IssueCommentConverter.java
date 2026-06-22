package com.lnf.sentinel.converter;

import com.lnf.dto.sentinel.IssueCommentDto;
import com.lnf.sentinel.model.IssueComment;

public class IssueCommentConverter {

    private IssueCommentConverter(){}

    public static IssueCommentDto toTransportModel(IssueComment entity){
        return IssueCommentDto.builder()
                .issueId(entity.getIssueId())
                .authorId(entity.getAuthorId())
                .body(entity.getBody())
                .internal(entity.isInternal())
                .build();
    }


    public IssueComment toEntityModel(IssueComment entity, IssueCommentDto dto){
        entity.setIssueId(dto.getIssueId());
        entity.setBody(dto.getBody());
        entity.setInternal(dto.isInternal());
        entity.setAuthorId(dto.getAuthorId());
        return entity;
    }
}
