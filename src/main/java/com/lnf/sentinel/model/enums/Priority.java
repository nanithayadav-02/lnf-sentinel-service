package com.lnf.sentinel.model.enums;

import lombok.Getter;

@Getter
public enum Priority {

    P1("P1"),
    P2("P2"),
    P3("P3"),
    P4("P4");

    private final String label;

    Priority(String label) {
        this.label = label;
    }

    public static Priority valueOfLabel(String label) {
        for (Priority at : values()) {
            if (at.label.equals(label)) {
                return at;
            }
        }
        return null;
    }

}
