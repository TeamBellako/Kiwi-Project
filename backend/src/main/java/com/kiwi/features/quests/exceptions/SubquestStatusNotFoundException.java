package com.kiwi.features.quests.exceptions;

import jakarta.validation.constraints.NotNull;

public class SubquestStatusNotFoundException extends RuntimeException {
    public SubquestStatusNotFoundException(@NotNull long id) {
        super(String.format("Cannot found status of subquest with id %s", id));
    }
}
