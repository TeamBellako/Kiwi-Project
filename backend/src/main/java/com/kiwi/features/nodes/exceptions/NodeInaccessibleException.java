package com.kiwi.features.nodes.exceptions;

import jakarta.validation.constraints.NotNull;

public class NodeInaccessibleException extends RuntimeException {
    public NodeInaccessibleException(@NotNull Long id) {
        super(String.format("Node with id %s is INACCESSIBLE", id));
    }
}
