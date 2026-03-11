package com.kiwi.features.combat.data.dto;

import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatDTO {

    private Long id;

    private Long combatConfigId;

    private int timeMax;
    private int timeRemaining;

    private int turnNumber;

    private CombatGeneralStatus combatStatus;

    private Instant startedAt;

    private UserCombatEntityDTO user;
    private EnemyCombatEntityDTO enemy;
}