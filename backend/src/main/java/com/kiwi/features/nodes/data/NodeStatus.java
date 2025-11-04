package com.kiwi.features.nodes.data;

public enum NodeStatus {
    INACCESSIBLE,
    LOCKED,
    OPEN,
    COMPLETED;

    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public boolean isUnlocked() {
        return this == LOCKED || this == OPEN || this == COMPLETED;
    }
}