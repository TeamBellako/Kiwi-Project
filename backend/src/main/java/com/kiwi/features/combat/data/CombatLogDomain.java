package com.kiwi.features.combat.data;

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