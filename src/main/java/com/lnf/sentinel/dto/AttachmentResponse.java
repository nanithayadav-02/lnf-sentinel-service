package com.lnf.sentinel.dto;

import com.lnf.sentinel.domain.IssueAttachment;

import java.time.OffsetDateTime;

public record AttachmentResponse(
        Long id,
        Long issueId,
        String fileName,
        String storageKey,
        String contentType,
        Long sizeBytes,
        Long uploadedBy,
        OffsetDateTime createdAt
) {
    public static AttachmentResponse from(IssueAttachment a) {
        return new AttachmentResponse(a.getId(), a.getIssueId(), a.getFileName(), a.getStorageKey(),
                a.getContentType(), a.getSizeBytes(), a.getUploadedBy(), a.getCreatedAt());
    }
}
