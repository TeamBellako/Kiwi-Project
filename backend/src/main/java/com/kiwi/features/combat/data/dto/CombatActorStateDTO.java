package com.kiwi.features.combat.data.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatActorStateDTO {

    private String actor; // USER / ENEMY

    private int hp;

    private List<CombatStateAppliedDTO> activeStates;
}