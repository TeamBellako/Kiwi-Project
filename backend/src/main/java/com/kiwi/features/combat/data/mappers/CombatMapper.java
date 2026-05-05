package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.domain.*;
import com.kiwi.features.combat.data.dto.*;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.data.persistence.CombatPersistence;
import com.kiwi.features.combat.data.persistence.EnemyPersistence;
import com.kiwi.features.combat.data.persistence.UserStatsPersistence;

import java.time.Instant;
import java.util.List;

public class CombatMapper {

    //------------------------------------------------------------------------------------------------------------------

    public static CombatDomain toDomain(CombatPersistence combat) {
        return CombatDomain.builder()
                .id(combat.getId())
                .combatConfigId(combat.getCombatConfigId())
                .userId(combat.getUserId())
                .enemyId(combat.getEnemyId())

                // USER
                .userHp(combat.getUserHp())
                .userMaxHp(combat.getUserMaxHp())
                .userPatk(combat.getUserPatk())
                .userMatk(combat.getUserMatk())
                .userPdef(combat.getUserPdef())
                .userMdef(combat.getUserMdef())
                .userAcc(combat.getUserAcc())
                .userEva(combat.getUserEva())
                .userLck(combat.getUserLck())

                // ENEMY
                .enemyHp(combat.getEnemyHp())
                .enemyMaxHp(combat.getEnemyMaxHp())
                .enemyPatk(combat.getEnemyPatk())
                .enemyMatk(combat.getEnemyMatk())
                .enemyPdef(combat.getEnemyPdef())
                .enemyMdef(combat.getEnemyMdef())
                .enemyAcc(combat.getEnemyAcc())
                .enemyEva(combat.getEnemyEva())
                .enemyLck(combat.getEnemyLck())

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
            String background,
            String sfx,
            List<CombatActionDTO> log
    ) {

        return CombatDTO.builder()
                .id(combat.getId())
                .combatConfigId(combat.getCombatConfigId())
                .background(background)
                .sfx(sfx)
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

    public static CombatPersistence toNewCombat(
            Long userId,
            Long configId,
            UserStatsPersistence stats,
            EnemyPersistence enemy,
            Instant endsAt
    ) {
        return CombatPersistence.builder()
                .userId(userId)
                .combatConfigId(configId)
                .enemyId(enemy.getId())

                // USER snapshot
                .userHp(stats.getMaxHp())
                .userMaxHp(stats.getMaxHp())
                .userPatk(stats.getPatk())
                .userMatk(stats.getMatk())
                .userPdef(stats.getPdef())
                .userMdef(stats.getMdef())
                .userAcc(stats.getAcc())
                .userEva(stats.getEva())
                .userLck(stats.getLck())

                // ENEMY snapshot
                .enemyHp(enemy.getMaxHp())
                .enemyMaxHp(enemy.getMaxHp())
                .enemyPatk(enemy.getPatk())
                .enemyMatk(enemy.getMatk())
                .enemyPdef(enemy.getPdef())
                .enemyMdef(enemy.getMdef())
                .enemyAcc(enemy.getAcc())
                .enemyEva(enemy.getEva())
                .enemyLck(enemy.getLck())

                .turnNumber(1)
                .combatStatus(CombatGeneralStatus.ONGOING)
                .endsAt(endsAt)
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

}