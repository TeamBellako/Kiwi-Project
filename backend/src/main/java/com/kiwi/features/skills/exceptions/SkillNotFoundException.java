package com.kiwi.features.skills.exceptions;

import jakarta.validation.constraints.NotNull;

public class SkillNotFoundException extends RuntimeException {
    public SkillNotFoundException(@NotNull Long id) {
        super(String.format("Cannot found skill with id %s", id));
    }
}