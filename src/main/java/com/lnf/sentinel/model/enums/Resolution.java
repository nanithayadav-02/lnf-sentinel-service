package com.lnf.sentinel.model.enums;

import lombok.Getter;

@Getter
public enum Resolution {

    FIXED("Fixed"),
    WONT_FIX("Won't Fix"),
    DUPLICATE("Duplicate"),
    CANNOT_REPRODUCE("Cannot Reproduce"),
    CONFIG_CHANGE("Config Change");

    private final String label;

    Resolution(String label) {
        this.label = label;
    }

    public static Resolution valueOfLabel(String label) {
        for (Resolution at : values()) {
            if (at.label.equals(label)) {
                return at;
            }
        }
        return null;
    }
}
