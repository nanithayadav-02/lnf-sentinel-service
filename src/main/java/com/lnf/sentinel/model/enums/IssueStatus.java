package com.lnf.sentinel.model.enums;

public enum IssueStatus {
    NEW, TRIAGED, IN_PROGRESS, AWAITING_TENANT, RESOLVED, CLOSED, REOPENED;

    /** Terminal states that require a resolution to be set. */
    public boolean isResolutionState() {
        return this == RESOLVED || this == CLOSED;
    }
}
