package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.domain.*;
import com.kiwi.features.combat.data.dto.*;
import com.kiwi.features.combat.data.persistence.CombatPersistence;

import java.util.List;

public class CombatMapper {

    //------------------------------------------------------------------------------------------------------------------

    public static CombatDomain toDomain(CombatPersistence combat) {
        return CombatDomain.builder()
                .id(combat.getId())
                .combatConfigId(combat.getCombatConfigId())
                .userId(combat.getUserId())
                .enemyId(combat.getEnemyId())
                .userHp(combat.getUserHp())
                .enemyHp(combat.getEnemyHp())
                .turnNumber(combat.getTurnNumber())
                .endsAt(combat.getEndsAt())
                .combatStatus(combat.getCombatStatus())
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    public static CombatDTO toDTO(
            CombatPersistence combat,
            CombatActorDTO user,
            CombatActorDTO enemy,
            String enemyName,
            String enemySprite,
            List<CombatActionDTO> log
    ) {

        return CombatDTO.builder()
                .id(combat.getId())
                .combatConfigId(combat.getCombatConfigId())
                .turnNumber(combat.getTurnNumber())
                .combatStatus(combat.getCombatStatus().toString())
                .endsAt(combat.getEndsAt() != null
                        ? combat.getEndsAt().toEpochMilli()
                        : null)
                .user(user)
                .enemy(enemy)
                .enemyName(enemyName)
                .enemySprite(enemySprite)
                .log(log)
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

}