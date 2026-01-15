package com.kiwi.features.skills.exceptions;

import jakarta.validation.constraints.NotNull;

public class SkillLevelUpNotFoundException extends RuntimeException {
    public SkillLevelUpNotFoundException(@NotNull Long skillId) {
        super(String.format("Cannot level up skill, next level skill not found for skill id %s", skillId));
    }
}