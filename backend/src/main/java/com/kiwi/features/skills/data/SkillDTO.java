package com.kiwi.features.skills.data;

import lombok.*;
import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class SkillDTO {

    private Long skillId;
    private String name;
    private String description;
    private String quote;
    private int icon;

    private String cooldownType;
    private Long cooldownGoalId;
    private Integer cooldownTimeMinutes;
    private String cooldownOtherDescription;
    private Long levelupSkillId;

    private boolean isCooldown;
    private Long cooldownUntil;
    private int deckSlot;
}
