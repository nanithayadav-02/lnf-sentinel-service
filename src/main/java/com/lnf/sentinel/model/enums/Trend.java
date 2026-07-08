package com.lnf.sentinel.model.enums;

public enum Trend {

    DOWN("Down"),
    SAME("Same"),
    UP("Up");

    private final String label;

    Trend(String label) {
        this.label = label;
    }

    public static Trend valueOfLabel(String label) {
        for (Trend at : values()) {
            if (at.label.equals(label)) {
                return at;
            }
        }
        return null;
    }

}
