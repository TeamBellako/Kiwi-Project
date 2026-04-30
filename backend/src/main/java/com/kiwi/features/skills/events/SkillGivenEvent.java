package com.kiwi.features.skills.events;

public record SkillGivenEvent(Long userId, Long skillId, Long cooldownGoalId) {}
