package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.dto.*;

import java.util.List;

public class CombatActorMapper {

    public static CombatActorDTO toDTO(Long id, int currentHP,
                                       StatsDTO statsDTO,
                                       List<ElementMultiplierDTO> actorElements,
                                       List<StatusResistanceDTO> actorResistances,
                                       List<CombatActiveStatusDTO> activeStatus) {
        return CombatActorDTO.builder()
                .actorId(id)
                .currentHp(currentHP)
                .stats(statsDTO)
                .elementalMultipliers(actorElements)
                .statusResistances(actorResistances)
                .activeStatus(activeStatus)
                .build();

    }

}
