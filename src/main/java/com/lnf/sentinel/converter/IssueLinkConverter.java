package com.lnf.sentinel.converter;

import com.lnf.dto.sentinel.IssueLinkDto;
import com.lnf.sentinel.domain.IssueLink;
import com.lnf.sentinel.domain.enums.LinkType;

public final class IssueLinkConverter {

    private IssueLinkConverter() {
    }

    public static IssueLinkDto toDto(IssueLink entity) {

        if (entity == null) {
            return null;
        }

        IssueLinkDto dto = new IssueLinkDto();

        dto.setId(entity.getId());
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