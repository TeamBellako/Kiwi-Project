package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.domain.CombatActorDomain;
import com.kiwi.features.combat.data.domain.StatsDomain;
import com.kiwi.features.combat.data.dto.CombatStatsDTO;
import com.kiwi.features.combat.data.persistence.CombatPersistence;
import com.kiwi.features.combat.data.persistence.EnemyPersistence;
import com.kiwi.features.combat.data.persistence.UserStatsPersistence;

public class StatsMapper {

    //------------------------------------------------------------------------------------------------------------------

    public static StatsDomain toDomainUser(CombatPersistence combat) {
        return StatsDomain.builder()
                .currentHp(combat.getUserHp())
                .maxHp(combat.getUserMaxHp())
                .patk(combat.getUserPatk())
                .matk(combat.getUserMatk())
                .pdef(combat.getUserPdef())
                .mdef(combat.getUserMdef())
                .acc(combat.getUserAcc())
                .eva(combat.getUserEva())
                .lck(combat.getUserLck())
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    public static StatsDomain toDomainEnemy(CombatPersistence combat) {
        return StatsDomain.builder()
                .currentHp(combat.getEnemyHp())
                .maxHp(combat.getEnemyMaxHp())
                .patk(combat.getEnemyPatk())
                .matk(combat.getEnemyMatk())
                .pdef(combat.getEnemyPdef())
                .mdef(combat.getEnemyMdef())
                .acc(combat.getEnemyAcc())
                .eva(combat.getEnemyEva())
                .lck(combat.getEnemyLck())
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    public static CombatStatsDTO toDTO(StatsDomain stats) {
        return CombatStatsDTO.builder()
                .currentHp(stats.getCurrentHp())
                .maxHp(stats.getMaxHp())
                .patk(stats.getPatk())
                .matk(stats.getMatk())
                .pdef(stats.getPdef())
                .mdef(stats.getMdef())
                .acc(stats.getAcc())
                .eva(stats.getEva())
                .lck(stats.getLck())
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

}
