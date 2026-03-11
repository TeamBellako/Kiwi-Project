package com.kiwi.features.combat.data.domain;

import com.kiwi.features.combat.data.enums.CombatActor;
import com.kiwi.features.combat.data.enums.CombatLogEffectType;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatLogDomain {

    private Long id;

    private Long combatId;

    private int turnNumber;

    private CombatActor actor;

    private Long skillId;

    private CombatActor target;

    private CombatLogEffectType effectType;

    private Float value;

    private Long stateId;

    private Integer statusDuration;

    private Instant createdAt;
}