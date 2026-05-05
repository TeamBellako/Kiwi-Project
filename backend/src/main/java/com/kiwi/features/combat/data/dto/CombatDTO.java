package com.kiwi.features.combat.data.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatDTO {

    private Long id;

    private Long combatConfigId;

    private String background;

    private String sfx;

    private int turnNumber;

    private Long endsAt;

    private String combatStatus;

    private String enemyName;
    private String enemySprite;

    private CombatActorDTO user;
    private CombatActorDTO enemy;

    private List<CombatActionDTO> log;

    @Builder.Default
    private List<CombatBarkTriggerDTO> barks = List.of();

    @Builder.Default
    private List<Long> firedBarkIds = List.of();

}