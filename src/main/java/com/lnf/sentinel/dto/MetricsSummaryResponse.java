package com.lnf.sentinel.dto;

/** Dashboard headline numbers, scoped to the current tenant when one is pinned. */
public record MetricsSummaryResponse(
        long openTotal,
        long s1Count,
        long s2Count,
        long inProgress,
        long awaitingTenant,
        long slaBreached
) {}
