package com.kiwi.features.skills.data.domain;

import com.kiwi.features.skills.data.enums.SkillType;
import com.kiwi.features.skills.data.enums.CooldownType;
import lombok.*;
import java.time.Instant;

@Getter
@Setter
public class SkillDomain {

    private Long skillId;
    private String name;
    private String description;
    private String quote;
    private SkillType type;

    private CooldownType cooldownType;
    private Long cooldownGoalId;
    private Integer cooldownTimeMinutes;
    private String cooldownOtherDescription;
    private Long levelupSkillId;

    private boolean isCooldown;
    private Instant cooldownUntil;
    private int deckSlot;

    public SkillDomain(Long id, String name, String description, String quote, SkillType skillType,
                       CooldownType cooldownType, Long cooldownGoalId, Integer cooldownTimeMinutes,
                       String cooldownOtherDescription, Long levelupSkillId, boolean isCooldown,
                       Instant cooldownUntil, int deckSlot) {
        this.skillId = id;
        this.name = name;
        this.description = description;
        this.quote = quote;
        this.type = skillType;
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
