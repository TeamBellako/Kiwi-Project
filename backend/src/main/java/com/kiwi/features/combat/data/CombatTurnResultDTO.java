package com.kiwi.features.combat.data;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatTurnResultDTO {

    private Long combatId;

    private int turnNumber;

    private CombatActorStateDTO user;
    private CombatActorStateDTO enemy;

    private List<CombatActionDTO> actions;

    private boolean combatEnded;
    private String combatStatus;
}