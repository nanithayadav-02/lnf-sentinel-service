package com.lnf.sentinel.model.enums;

import lombok.Getter;

@Getter
public enum UserRole {

    ADMIN("Admin"),
    ENGINEER("Engineer"),
    SUPPORT("Support"),
    VIEWER("Viewer");

    private final String label;

    UserRole(String label) {
        this.label = label;
    }

    public static UserRole valueOfLabel(String label) {
        for (UserRole at : values()) {
            if (at.label.equals(label)) {
                return at;
            }
        }
        return null;
    }

}
