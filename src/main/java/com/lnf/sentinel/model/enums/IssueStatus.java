package com.lnf.sentinel.model.enums;

import lombok.Getter;

@Getter
public enum IssueStatus {

    NEW("New"),
    TRIAGED("Triaged"),
    IN_PROGRESS("In Progress"),
    AWAITING_TENANT("Awaiting Tenant"),
    RESOLVED("Resolved"),
    CLOSED("Closed"),
    REOPENED("Reopened");

    private final String label;

    IssueStatus(String label) {
        this.label = label;
    }

    /**
     * Terminal states that require a resolution to be set.
     */
    public boolean isResolutionState() {
        return this == RESOLVED || this == CLOSED;
    }

    public static IssueStatus valueOfLabel(String label) {
        for (IssueStatus at : values()) {
            if (at.label.equals(label)) {
                return at;
            }
        }
        return null;
    }

}
