package com.lnf.sentinel.model.enums;

import lombok.Getter;

@Getter
public enum TenantStatus {

    ONBOARDING("Onboarding"),
    ACTIVE("Active"),
    SUSPENDED("Suspended"),
    OFFBOARDED("Offboarded");

    private final String label;

    TenantStatus(String label) {
        this.label = label;
    }

    public static TenantStatus valueOfLabel(String label) {
        for (TenantStatus at : values()) {
            if (at.label.equals(label)) {
                return at;
            }
        }
        return null;
    }

}
