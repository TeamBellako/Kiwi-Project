package com.kiwi.features.skills.data;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
public class SkillDomain {

    private Long skillId;
    private String name;
    private String description;
    private String quote;
    private int icon;

    private CooldownType cooldownType;
    private Long cooldownGoalId;
    private Integer cooldownTimeMinutes;
    private String cooldownOtherDescription;
    private Long levelupSkillId;

    private boolean isCooldown;
    private Instant cooldownUntil;
    private int deckSlot;

    public SkillDomain(
            Long skillId,
            String name,
            String description,
            String quote,
            int icon,
            CooldownType cooldownType,
            Long cooldownGoalId,
            Integer cooldownTimeMinutes,
            String cooldownOtherDescription,
            Long levelupSkillId,
            boolean isCooldown,
            Instant cooldownUntil,
            int deckSlot
    ) {
        this.skillId = skillId;
        this.name = name;
        this.description = description;
        this.quote = quote;
        this.icon = icon;
        this.cooldownType = cooldownType;
        this.cooldownGoalId = cooldownGoalId;
        this.cooldownTimeMinutes = cooldownTimeMinutes;
        this.cooldownOtherDescription = cooldownOtherDescription;
        this.levelupSkillId = levelupSkillId;
        this.isCooldown = isCooldown;
        this.cooldownUntil = cooldownUntil;
        this.deckSlot = deckSlot;
    }
}
