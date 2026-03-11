package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.dto.CombatDTO;
import com.kiwi.features.combat.data.dto.EnemyCombatEntityDTO;
import com.kiwi.features.combat.data.persistence.CombatPersistence;
import com.kiwi.features.combat.data.dto.UserCombatEntityDTO;

public class CombatMapper {

    public static CombatDTO toDTO(
            CombatPersistence combat,
            UserCombatEntityDTO user,
            EnemyCombatEntityDTO enemy
    ) {

        return CombatDTO.builder()
                .id(combat.getId())
                .combatConfigId(combat.getCombatConfigId())
                .timeMax(combat.getTimeMax())
                .timeRemaining(combat.getTimeRemaining())
                .turnNumber(combat.getTurnNumber())
                .combatStatus(combat.getCombatStatus())
                .startedAt(combat.getStartedAt())
                .user(user)
                .enemy(enemy)
                .build();
    }
}