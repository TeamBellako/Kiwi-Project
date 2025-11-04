package com.kiwi.features.nodes.exceptions;

import jakarta.validation.constraints.NotNull;

public class NodeNotFoundException extends RuntimeException {
    public NodeNotFoundException(@NotNull int id) {
        super(String.format("Cannot found node by if %s", id));
    }
}
