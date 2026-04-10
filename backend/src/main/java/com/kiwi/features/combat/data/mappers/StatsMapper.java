package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.dto.StatsDTO;
import com.kiwi.features.combat.data.persistence.EnemyPersistence;
import com.kiwi.features.combat.data.persistence.UserStatsPersistence;

public class StatsMapper {

    public static StatsDTO toDTO(UserStatsPersistence  userStatsPersistence)
    {
        return StatsDTO.builder()
            .maxHp(userStatsPersistence.getMaxHp())
            .patk(userStatsPersistence.getPatk())
            .matk(userStatsPersistence.getMatk())
            .pdef(userStatsPersistence.getPdef())
            .mdef(userStatsPersistence.getMdef())
            .acc(userStatsPersistence.getAcc())
            .eva(userStatsPersistence.getEva())
            .lck(userStatsPersistence.getLck())
            .build();
    }

    public static StatsDTO toDTO(EnemyPersistence enemyPersistence)
    {
        return StatsDTO.builder()
                .maxHp(enemyPersistence.getMaxHp())
                .patk(enemyPersistence.getPatk())
                .matk(enemyPersistence.getMatk())
                .pdef(enemyPersistence.getPdef())
                .mdef(enemyPersistence.getMdef())
                .acc(enemyPersistence.getAcc())
                .eva(enemyPersistence.getEva())
                .lck(enemyPersistence.getLck())
                .build();
    }
}
