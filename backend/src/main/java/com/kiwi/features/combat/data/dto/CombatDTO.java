package com.kiwi.features.combat.data.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatDTO {

    private Long id;

    private Long combatConfigId;

    private int turnNumber;

    private Long endsAt;

    private String combatStatus;

    private UserActorDTO user;
    private EnemyActorDTO enemy;

    //TODO faltan los combatstateapplied y el log (vacios si empieza el combate)
}