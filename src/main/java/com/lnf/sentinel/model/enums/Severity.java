package com.lnf.sentinel.model.enums;

import lombok.Getter;

@Getter
public enum Severity {

    S1_CRITICAL("S1 Critical"),
    S2_HIGH("S2 High"),
    S3_MEDIUM("S3 Medium"),
    S4_LOW("S4 Low");

    private final String label;

    Severity(String label) {
        this.label = label;
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
