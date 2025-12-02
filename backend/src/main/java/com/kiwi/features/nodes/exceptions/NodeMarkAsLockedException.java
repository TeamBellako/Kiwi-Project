package com.kiwi.features.nodes.exceptions;

import jakarta.validation.constraints.NotNull;

public class NodeMarkAsLockedException extends RuntimeException {
    public NodeMarkAsLockedException(@NotNull int id) {
        super(String.format("Cannot mark next nodes as locked because node with id %s is not COMPLETED", id));
    }
}
