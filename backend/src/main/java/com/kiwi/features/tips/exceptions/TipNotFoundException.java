package com.kiwi.features.tips.exceptions;

import jakarta.validation.constraints.NotNull;

public class TipNotFoundException extends RuntimeException {
    public TipNotFoundException(@NotNull Long id) {
        super(String.format("Cannot found tip with id %s", id));
    }
}