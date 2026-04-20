package com.kiwi.features.combat.data.enums;

public enum CombatActionType {
    SKILL_USED,
    ACTOR_BLOCKED_BY_STATE,          // FREEZE
    SKILL_REPEAT_BY_STATE,    // CONFUSION
    ACTOR_DAMAGED_BY_STATE,
    BLOCKED_SKILLS_BY_STATE,
    RELEASED_SKILLS_BY_STATE,
    SKIP,
    STATUS_TURN_REDUCED,
    STATUS_FINISHED,
    TIMEOUT,
    ABANDON,
}
