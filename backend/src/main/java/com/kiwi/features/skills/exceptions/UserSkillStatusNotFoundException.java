package com.kiwi.features.skills.exceptions;

import jakarta.validation.constraints.NotNull;

public class UserSkillStatusNotFoundException extends RuntimeException {
    public UserSkillStatusNotFoundException(@NotNull Long userId, @NotNull Long skillId) {
        super(String.format("Cannot find user skill status for user %s and skill %s", userId, skillId));
    }
}