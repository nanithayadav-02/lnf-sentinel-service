package com.lnf.sentinel.dto;

import jakarta.validation.constraints.NotBlank;

/** Attachment metadata. The binary itself lives in blob storage (storageKey). */
public record CreateAttachmentRequest(
        @NotBlank String fileName,
        @NotBlank String storageKey,
        String contentType,
        Long sizeBytes,
        Long uploadedBy
) {}
