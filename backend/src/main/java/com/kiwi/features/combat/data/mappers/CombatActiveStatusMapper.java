package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.domain.CombatActiveStatusDomain;
import com.kiwi.features.combat.data.dto.CombatActiveStatusDTO;
import com.kiwi.features.combat.data.persistence.CombatActiveStatusPersistence;
import com.kiwi.features.combat.data.persistence.CombatStatePersistence;

import java.util.List;

public class CombatActiveStatusMapper {

    public static CombatActiveStatusDomain toDomain (CombatActiveStatusPersistence activeStatusPersistence, CombatStatePersistence statePersistence) {

        return CombatActiveStatusDomain.builder()
                .stateId(activeStatusPersistence.getStateId())
                .name(statePersistence.getName())
                .icon(statePersistence.getIcon())
                .description(statePersistence.getDescription())
                .remainingTurns(activeStatusPersistence.getRemainingTurns())
                .value(activeStatusPersistence.getValue())
                .build();
    }

   public static CombatActiveStatusDTO toDTO (CombatActiveStatusDomain domain){

       return CombatActiveStatusDTO.builder()
                .stateId(domain.getStateId())
                .name(domain.getName())
                .icon(domain.getIcon())
                .description(domain.getDescription())
                .remainingTurns(domain.getRemainingTurns())
                .value(domain.getValue())
                .build();
   }

    public static List<CombatActiveStatusDTO> toDTOList(List<CombatActiveStatusDomain> list) {
        return list.stream()
                .map(CombatActiveStatusMapper::toDTO)
                .toList();
    }

}
