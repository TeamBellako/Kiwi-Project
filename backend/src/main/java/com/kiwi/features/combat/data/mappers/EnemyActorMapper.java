package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.dto.EnemyActorDTO;
import com.kiwi.features.combat.data.dto.EnemyStatsDTO;
import com.kiwi.features.combat.data.dto.ElementMultiplierDTO;
import com.kiwi.features.combat.data.dto.StatusResistanceDTO;
import com.kiwi.features.combat.data.persistence.EnemyPersistence;

import java.util.List;

public class EnemyActorMapper {

    public static EnemyActorDTO toDTO(
            EnemyPersistence enemy,
            int currentHp,
            List<ElementMultiplierDTO> elements,
            List<StatusResistanceDTO> resistances
    ) {

        EnemyStatsDTO stats = EnemyStatsDTO.builder()
                .patk(enemy.getPatk())
                .matk(enemy.getMatk())
                .pdef(enemy.getPdef())
                .mdef(enemy.getMdef())
                .acc(enemy.getAcc())
                .eva(enemy.getEva())
                .lck(enemy.getLck())
                .build();

        return EnemyActorDTO.builder()
                .enemyId(enemy.getId())
                .name(enemy.getName())
                .currentHp(currentHp)
                .maxHp(enemy.getMaxHp())
                .stats(stats)
                .elementalMultipliers(elements)
                .statusResistances(resistances)
                .build();
    }
}