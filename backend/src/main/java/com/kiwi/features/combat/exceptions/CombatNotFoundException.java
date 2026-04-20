package com.kiwi.features.combat.exceptions;

import jakarta.validation.constraints.NotNull;


public class CombatNotFoundException  extends RuntimeException {

    public CombatNotFoundException(@NotNull Long id) {
        super(String.format("Cannot found combat with id %s", id));
    }
}
