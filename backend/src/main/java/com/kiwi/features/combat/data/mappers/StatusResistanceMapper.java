package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.domain.StatusResistanceDomain;
import com.kiwi.features.combat.data.dto.StatusResistanceDTO;
import com.kiwi.features.combat.data.persistence.CombatStatePersistence;
import com.kiwi.features.combat.data.persistence.EnemyStatusResistancePersistence;
import com.kiwi.features.combat.data.persistence.UserStatusResistancePersistence;

import java.util.List;

public class StatusResistanceMapper {

    //------------------------------------------------------------------------------------------------------------------

    public static StatusResistanceDTO toDTO(StatusResistanceDomain state) {
        return StatusResistanceDTO.builder()
                .stateId(state.getStateId())
                .stateName(state.getStateName())
                .stateIcon(state.getStateIcon())
                .resistance(state.getResistance())
                .stateDescription(state.getStateDescription())
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    public static List<StatusResistanceDTO> toDTOList(List<StatusResistanceDomain> list) {
        return list.stream()
                .map(StatusResistanceMapper::toDTO)
                .toList();
    }

    //------------------------------------------------------------------------------------------------------------------

    public static StatusResistanceDomain toDomain(EnemyStatusResistancePersistence resistance, CombatStatePersistence state) {
        return StatusResistanceDomain.builder()
                .stateId(state.getId())
                .stateName(state.getName())
                .stateIcon(state.getIcon())
                .resistance(resistance.getResistance())
                .stateDescription(state.getDescription())
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    public static StatusResistanceDomain toDomain(UserStatusResistancePersistence resistance, CombatStatePersistence state) {
        return StatusResistanceDomain.builder()
                .stateId(state.getId())
                .stateName(state.getName())
                .stateIcon(state.getIcon())
                .resistance(resistance.getResistance())
                .stateDescription(state.getDescription())
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

}
