package com.kiwi.features.nodes.exceptions;

import jakarta.validation.constraints.NotNull;

public class NodeNotFoundException extends RuntimeException {
    public NodeNotFoundException(@NotNull Long id) {
        super(String.format("Cannot found node with id %s", id));
    }
}
