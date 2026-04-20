package com.kiwi.features.combat.exceptions;

import jakarta.validation.constraints.NotNull;

public class CombatFinishedException extends RuntimeException  {

    public CombatFinishedException(@NotNull Long id) {
        super(String.format("Combat with id %s already finished", id));
    }
}