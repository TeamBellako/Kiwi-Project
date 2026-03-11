package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.dto.UserCombatEntityDTO;
import com.kiwi.features.combat.data.dto.UserStatsDTO;
import com.kiwi.features.combat.data.dto.ElementMultiplierDTO;
import com.kiwi.features.combat.data.dto.StatusResistanceDTO;
import com.kiwi.features.combat.data.persistence.UserStatsPersistence;

import java.util.List;

public class UserCombatEntityMapper {

    public static UserCombatEntityDTO toDTO(
            Long userId,
            int currentHp,
            UserStatsPersistence stats,
            List<ElementMultiplierDTO> elements,
            List<StatusResistanceDTO> resistances
    ) {

        UserStatsDTO statsDTO = UserStatsDTO.builder()
                .patk(stats.getPatk())
                .matk(stats.getMatk())
                .pdef(stats.getPdef())
                .mdef(stats.getMdef())
                .acc(stats.getAcc())
                .eva(stats.getEva())
                .lck(stats.getLck())
                .build();

        return UserCombatEntityDTO.builder()
                .userId(userId)
                .currentHp(currentHp)
                .maxHp(stats.getMaxHp())
                .stats(statsDTO)
                .elementalMultipliers(elements)
                .statusResistances(resistances)
                .build();
    }
}