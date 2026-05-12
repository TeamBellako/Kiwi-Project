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

    public static CombatDTO toDTO(
            CombatPersistence combat,
            CombatActorDTO user,
            CombatActorDTO enemy,
            String enemyName,
            String enemySprite,
            String background,
            String sfx,
            List<CombatActionDTO> log,
            List<CombatBarkTriggerDTO> barks,
            List<Long> firedBarkIds
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
                .barks(barks)
                .firedBarkIds(firedBarkIds)
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
                .userShield(stats.getShield())
                .userPatk(stats.getPatk())
                .userMatk(stats.getMatk())
                .userPdef(stats.getPdef())
                .userMdef(stats.getMdef())
                .userAcc(stats.getAcc())
                .userEva(stats.getEva())
                .userLck(stats.getLck())
                .userTurns(0)

                // ENEMY snapshot
                .enemyHp(enemy.getMaxHp())
                .enemyMaxHp(enemy.getMaxHp())
                .enemyShield(enemy.getShield())
                .enemyPatk(enemy.getPatk())
                .enemyMatk(enemy.getMatk())
                .enemyPdef(enemy.getPdef())
                .enemyMdef(enemy.getMdef())
                .enemyAcc(enemy.getAcc())
                .enemyEva(enemy.getEva())
                .enemyLck(enemy.getLck())
                .enemyTurns(0)

                .turnNumber(1)
                .combatStatus(CombatGeneralStatus.ONGOING)
                .endsAt(endsAt)
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

}