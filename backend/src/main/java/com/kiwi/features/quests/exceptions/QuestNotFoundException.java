package com.kiwi.features.quests.exceptions;

import jakarta.validation.constraints.NotNull;

public class QuestNotFoundException extends RuntimeException {
    public QuestNotFoundException(@NotNull int id) {
        super(String.format("Cannot found quest with id %s", id));
    }
}
