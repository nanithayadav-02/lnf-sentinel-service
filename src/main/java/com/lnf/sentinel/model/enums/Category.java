package com.lnf.sentinel.model.enums;

import lombok.Getter;

@Getter
public enum Category {
    OUTAGE("Outage"),
    BUG("Bug"),
    PERFORMANCE("Performance"),
    DATA("Data"),
    SECURITY("Security"),
    CONFIG("Config"),
    OTHER("Other");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    public static Category valueOfLabel(String label) {
        for (Category at : values()) {
            if (at.label.equals(label)) {
                return at;
            }
        }
        return null;
    }

}
