package com.lnf.sentinel.model.enums;

import lombok.Getter;

@Getter
public enum Environment {

    PRODUCTION("Production"),
    STAGING("Staging"),
    DR("DR");

    private final String label;

    Environment(String label) {
        this.label = label;
    }

    public static Environment valueOfLabel(String label) {
        for (Environment at : values()) {
            if (at.label.equals(label)) {
                return at;
            }
        }
        return null;
    }

}
