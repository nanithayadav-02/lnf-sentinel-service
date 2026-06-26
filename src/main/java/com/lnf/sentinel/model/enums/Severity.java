package com.lnf.sentinel.model.enums;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum Severity {

    S1_CRITICAL("S1 Critical", Duration.ofHours(4)),
    S2_HIGH("S2 High", Duration.ofHours(8)),
    S3_MEDIUM("S3 Medium", Duration.ofHours(24)),
    S4_LOW("S4 Low", Duration.ofHours(72));

    private final String label;
    private final Duration slaTarget;

    Severity(String label, Duration slaTarget) {
        this.label = label;
        this.slaTarget = slaTarget;
    }

    public Duration slaTarget() {
        return slaTarget;
    }

    public static Severity valueOfLabel(String label) {
        for (Severity at : values()) {
            if (at.label.equals(label)) {
                return at;
            }
        }
        return null;
    }

}
