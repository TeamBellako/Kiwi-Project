package com.kiwi.features.combat.exceptions;

import jakarta.validation.constraints.NotNull;

public class NotTimedCombatException extends RuntimeException  {

    public NotTimedCombatException(@NotNull Long id) {
        super(String.format("Combat with id %s is not a timed combat", id));
    }
}
