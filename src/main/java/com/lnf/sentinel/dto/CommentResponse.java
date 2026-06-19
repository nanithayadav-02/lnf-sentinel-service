package com.lnf.sentinel.dto;

import com.lnf.sentinel.domain.IssueComment;

import java.time.OffsetDateTime;

public record CommentResponse(
        Long id,
        Long issueId,
        Long authorId,
        String body,
        boolean internal,
        OffsetDateTime createdAt
) {
    public static CommentResponse from(IssueComment c) {
        return new CommentResponse(c.getId(), c.getIssueId(), c.getAuthorId(),
                c.getBody(), c.isInternal(), c.getCreatedAt());
    }
}
