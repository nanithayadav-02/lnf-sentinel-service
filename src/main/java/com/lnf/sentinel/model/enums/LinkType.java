package com.lnf.sentinel.model.enums;

public enum LinkType {
    BLOCKS, BLOCKED_BY, DUPLICATES, RELATES_TO, CAUSED_BY;

    /** The inverse direction, used when presenting a link from the target side. */
    public LinkType inverse() {
        return switch (this) {
            case BLOCKS -> BLOCKED_BY;
            case BLOCKED_BY -> BLOCKS;
            case DUPLICATES, RELATES_TO, CAUSED_BY -> this;
        };
    }
}
