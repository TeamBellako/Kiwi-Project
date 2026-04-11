package com.kiwi.features.combat.exceptions;

import jakarta.validation.constraints.NotNull;

public class CombatStateNotFoundException  extends RuntimeException {

    public CombatStateNotFoundException(@NotNull Long id) {
        super(String.format("Cannot found combat state with id %s", id));
    }
}
