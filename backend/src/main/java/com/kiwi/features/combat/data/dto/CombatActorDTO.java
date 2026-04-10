package com.kiwi.features.combat.data.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatActorDTO {

    private Long actorId;

    private int currentHp;

    private StatsDTO stats;

    private List<ElementMultiplierDTO> elementalMultipliers;

    private List<StatusResistanceDTO> statusResistances;

    private List<CombatActiveStatusDTO> activeStatus;
}