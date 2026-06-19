package com.lnf.sentinel.domain.enums;

import java.time.Duration;

/** Issue severity. Each level carries its SLA resolution target. */
public enum Severity {
    S1_CRITICAL(Duration.ofHours(4)),
    S2_HIGH(Duration.ofHours(8)),
    S3_MEDIUM(Duration.ofHours(24)),
    S4_LOW(Duration.ofHours(72));

    private final Duration slaTarget;

    Severity(Duration slaTarget) {
        this.slaTarget = slaTarget;
    }

    public Duration slaTarget() {
        return slaTarget;
    }
}
