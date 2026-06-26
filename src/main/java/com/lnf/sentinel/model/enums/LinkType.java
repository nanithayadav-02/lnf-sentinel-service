package com.lnf.sentinel.model.enums;

import lombok.Getter;

@Getter
public enum LinkType {

    BLOCKS("Blocks"),
    BLOCKED_BY("Blocked By"),
    DUPLICATES("Duplicates"),
    RELATES_TO("Relates To"),
    CAUSED_BY("Caused By");

    private final String label;

    LinkType(String label) {
        this.label = label;
    }

    /** The inverse direction, used when presenting a link from the target side. */
    public LinkType inverse() {
        return switch (this) {
            case BLOCKS -> BLOCKED_BY;
            case BLOCKED_BY -> BLOCKS;
            case DUPLICATES, RELATES_TO, CAUSED_BY -> this;
        };
    }

    public static LinkType valueOfLabel(String label) {
        for (LinkType at : values()) {
            if (at.label.equals(label)) {
                return at;
            }
        }
        return null;
    }

}