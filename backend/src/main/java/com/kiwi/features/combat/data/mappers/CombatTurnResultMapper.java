package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.domain.CombatTurnResultDomain;
import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.dto.CombatTurnResultDTO;

import java.time.Instant;
import java.util.List;

public class CombatTurnResultMapper {

    public static CombatTurnResultDTO toDTO (CombatTurnResultDomain domain, Instant createdAt){

        List<CombatActionDTO> actionDTOList = CombatActionMapper.toDTOList(domain.getActions());

        return CombatTurnResultDTO.builder()
                .combatId(domain.getCombatId())
                .turnNumber(domain.getTurnNumber())
                .actions(actionDTOList)
                .combatStatus(domain.getCombatStatus().name())
                .bonusActionPending(domain.isBonusActionPending())
                .createdAt(createdAt)
                .build();
    }
}
