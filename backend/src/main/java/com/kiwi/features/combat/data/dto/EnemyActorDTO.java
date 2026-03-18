package com.kiwi.features.combat.data.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnemyActorDTO {

    private Long enemyId;

    private String name;
    private String sprite;

    private int currentHp;
    private int maxHp;

    private EnemyStatsDTO stats;

    private List<ElementMultiplierDTO> elementalMultipliers;

    private List<StatusResistanceDTO> statusResistances;
}