package com.lnf.sentinel.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(
        @NotBlank String body,
        Long authorId,
        Boolean internal
) {}
