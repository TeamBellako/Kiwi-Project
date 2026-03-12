package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.dto.CombatDTO;
import com.kiwi.features.combat.data.dto.EnemyActorDTO;
import com.kiwi.features.combat.data.persistence.CombatPersistence;
import com.kiwi.features.combat.data.dto.UserActorDTO;

public class CombatMapper {

    public static CombatDTO toDTO(
            CombatPersistence combat,
            UserActorDTO user,
            EnemyActorDTO enemy
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