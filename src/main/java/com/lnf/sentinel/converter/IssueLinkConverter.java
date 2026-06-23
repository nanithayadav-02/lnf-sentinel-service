package com.lnf.sentinel.converter;

import com.lnf.dto.sentinel.IssueLinkDto;
import com.lnf.sentinel.model.IssueLink;
import com.lnf.sentinel.model. enums.LinkType;

public final class IssueLinkConverter {

    private IssueLinkConverter() {
    }

    public static IssueLinkDto toDto(IssueLink entity) {

        if (entity == null) {
            return null;
        }

        IssueLinkDto dto = new IssueLinkDto();


        dto.setSourceIssueId(entity.getSourceIssueId());
        dto.setTargetIssueId(entity.getTargetIssueId());

        dto.setLinkType(
                entity.getLinkType() != null
                        ? entity.getLinkType().name()
                        : null
        );

        return dto;
    }

    public static IssueLink toEntity(IssueLinkDto dto, IssueLink entity) {

        if (dto == null || entity == null) {
            return null;
        }

        entity.setId(dto.getId());
        entity.setSourceIssueId(dto.getSourceIssueId());
        entity.setTargetIssueId(dto.getTargetIssueId());

        if (dto.getLinkType() != null) {
            entity.setLinkType(LinkType.valueOf(dto.getLinkType()));
        }

        return entity;
    }
}