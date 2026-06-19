package com.lnf.sentinel.dto;

import com.lnf.sentinel.domain.IssueWatcher;

import java.time.OffsetDateTime;

public record WatcherResponse(Long id, Long issueId, Long userId, OffsetDateTime createdAt) {
    public static WatcherResponse from(IssueWatcher w) {
        return new WatcherResponse(w.getId(), w.getIssueId(), w.getUserId(), w.getCreatedAt());
    }
}
