package com.lnf.sentinel.model.enums;

import lombok.Getter;

@Getter
public enum SupportTier {

    BASIC("Basic"),
    STANDARD("Standard"),
    PREMIUM("Premium"),
    ENTERPRISE("Enterprise");

    private final String label;

    SupportTier(String label) {
        this.label = label;
    }

    public static SupportTier valueOfLabel(String label) {
        for (SupportTier at : values()) {
            if (at.label.equals(label)) {
                return at;
            }
        }
        return null;
    }

}
