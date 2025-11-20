package com.kiwi.features.quests.exceptions;

import jakarta.validation.constraints.NotNull;

public class SubquestNotFoundException extends RuntimeException {
    public SubquestNotFoundException(@NotNull long id) {
        super(String.format("Cannot found subquest with id %s", id));
    }
}
