package com.kiwi.features.skills.data;

import java.time.Instant;

public interface UserSkillView {

    Long getSkillId();
    String getName();
    String getDescription();
    String getQuote();
    int getIcon();

    String getCooldownType();
    Long getCooldownGoalId();
    Integer getCooldownTimeMinutes();
    String getCooldownOtherDescription();
    Long getLevelupSkillId();

    Boolean getIsCooldown();
    Instant getCooldownUntil();
    int getDeckSlot();
}
