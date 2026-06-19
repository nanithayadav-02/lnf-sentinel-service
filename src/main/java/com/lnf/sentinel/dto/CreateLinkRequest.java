package com.lnf.sentinel.dto;

import com.lnf.sentinel.domain.enums.LinkType;
import jakarta.validation.constraints.NotNull;

public record CreateLinkRequest(
        @NotNull Long targetIssueId,
        @NotNull LinkType linkType,
        Long createdBy
) {}
