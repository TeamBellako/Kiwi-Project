package com.kiwi.features.nodes.exceptions;

import jakarta.validation.constraints.NotNull;

public class NodeSatusNotFoundException extends RuntimeException {
    public NodeSatusNotFoundException(@NotNull int id) {
        super(String.format("Cannot found status of node with id %s", id));
    }
}
