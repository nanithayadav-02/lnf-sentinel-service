package com.lnf.sentinel.dto;

import com.lnf.sentinel.domain.enums.*;
import jakarta.validation.constraints.Size;

/** Patch of mutable issue fields. Null fields are left unchanged. */
public record UpdateIssueRequest(
        @Size(max = 240) String title,
        String description,
        Severity severity,
        Priority priority,
        Category category,
        Environment environment,
        @Size(max = 120) String affectedService,
        Long assigneeId,
        @Size(max = 60) String fixVersion
) {}
