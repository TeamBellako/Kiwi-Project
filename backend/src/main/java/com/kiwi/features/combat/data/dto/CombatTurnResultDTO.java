package com.kiwi.features.combat.data.dto;

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

    private List<CombatActionDTO> actions;

    private String combatStatus;

}