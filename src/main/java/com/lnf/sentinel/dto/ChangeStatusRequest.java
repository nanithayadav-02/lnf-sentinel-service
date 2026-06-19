package com.lnf.sentinel.dto;

import com.lnf.sentinel.domain.enums.IssueStatus;
import com.lnf.sentinel.domain.enums.Resolution;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Change an issue's status. Moving to RESOLVED/CLOSED requires a {@code resolution}
 * and may capture {@code rootCause}/{@code fixVersion}.
 */
public record ChangeStatusRequest(
        @NotNull IssueStatus status,
        Resolution resolution,
        String rootCause,
        @Size(max = 60) String fixVersion,
        @Size(max = 500) String note,
        Long changedBy
) {}
