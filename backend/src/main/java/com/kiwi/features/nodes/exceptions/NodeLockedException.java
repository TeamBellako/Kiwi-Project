package com.kiwi.features.nodes.exceptions;

import jakarta.validation.constraints.NotNull;

public class NodeLockedException extends RuntimeException {
    public NodeLockedException(@NotNull int id) {
        super(String.format("Cannot unlock node with id %s", id));
    }
}
