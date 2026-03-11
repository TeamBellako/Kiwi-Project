package com.kiwi.features.combat.data.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCombatEntityDTO {

    private Long userId;

    private int currentHp;
    private int maxHp;

    private UserStatsDTO stats;

    private List<ElementMultiplierDTO> elementalMultipliers;

    private List<StatusResistanceDTO> statusResistances;
}