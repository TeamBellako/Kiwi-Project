package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.domain.CombatActorDomain;
import com.kiwi.features.combat.data.dto.CombatStatsDTO;
import com.kiwi.features.combat.data.persistence.EnemyPersistence;
import com.kiwi.features.combat.data.persistence.UserStatsPersistence;

public class StatsMapper {

    //------------------------------------------------------------------------------------------------------------------

    public static CombatStatsDTO toDTO(CombatActorDomain actor) {
        return CombatStatsDTO.builder()
                .maxHp(actor.getMaxHp())
                .patk(actor.getPatk())
                .matk(actor.getMatk())
                .pdef(actor.getPdef())
                .mdef(actor.getMdef())
                .acc(actor.getAcc())
                .eva(actor.getEva())
                .lck(actor.getLck())
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

}
