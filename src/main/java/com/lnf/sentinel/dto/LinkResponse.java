package com.lnf.sentinel.dto;

import com.lnf.sentinel.domain.IssueLink;
import com.lnf.sentinel.domain.enums.LinkType;

import java.time.OffsetDateTime;

/**
 * A link as seen from a given issue. {@code direction} is the link type relative
 * to the issue being viewed (inverted when the issue is the target).
 */
public record LinkResponse(
        Long id,
        Long sourceIssueId,
        Long targetIssueId,
        LinkType linkType,
        LinkType direction,
        Long createdBy,
        OffsetDateTime createdAt
) {
    public static LinkResponse from(IssueLink l, Long viewedFromIssueId) {
        LinkType direction = l.getSourceIssueId().equals(viewedFromIssueId)
                ? l.getLinkType()
                : l.getLinkType().inverse();
        return new LinkResponse(l.getId(), l.getSourceIssueId(), l.getTargetIssueId(),
                l.getLinkType(), direction, l.getCreatedBy(), l.getCreatedAt());
    }
}
