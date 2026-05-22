package com.kiwi.combat;

import com.kiwi.features.combat.data.dto.CombatDTO;
import com.kiwi.features.combat.data.dto.CombatTurnResultDTO;
import com.kiwi.features.combat.data.enums.BarkDismissMode;
import com.kiwi.features.combat.data.enums.BarkTriggerType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.enums.CombatActionType;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.data.persistence.*;

import java.time.Instant;
import java.util.List;
import java.util.Random;

public class CombatTestFactory {

    private static final Random RANDOM = new Random();

    private static int randStat() {
        return 50 + RANDOM.nextInt(100);
    }

    private static int randHp() {
        return 200 + RANDOM.nextInt(800);
    }

    // =========================================================================
    // USER STATS
    // =========================================================================

    public static UserStatsPersistence userStats(Long userId) {
        return UserStatsPersistence.builder()
                .userId(userId)
                .maxHp(randHp())
                .shield(randStat())
                .patk(randStat())
                .matk(randStat())
                .pdef(randStat())
                .mdef(randStat())
                .acc(randStat())
                .eva(randStat())
                .lck(randStat())
                .build();
    }

    // =========================================================================
    // ENEMY
    // =========================================================================

    public static EnemyPersistence enemy(Long id) {
        return EnemyPersistence.builder()
                .id(id)
                .name("Enemy " + id)
                .sprite("sprite_" + id)
                .maxHp(randHp())
                .shield(randStat())
                .patk(randStat())
                .matk(randStat())
                .pdef(randStat())
                .mdef(randStat())
                .acc(randStat())
                .eva(randStat())
                .lck(randStat())
                .build();
    }

    // =========================================================================
    // COMBAT CONFIG
    // =========================================================================

    public static CombatConfigPersistence combatConfig(Long id, Long enemyId) {
        return CombatConfigPersistence.builder()
                .id(id)
                .enemyId(enemyId)
                .timeLimit(5 + RANDOM.nextInt(30))
                .build();
    }

    // =========================================================================
    // COMBAT
    // =========================================================================

    public static CombatPersistence ongoingCombat(
            Long userId,
            Long configId,
            UserStatsPersistence stats,
            EnemyPersistence enemy
    ) {
        return combat(userId, configId, stats, enemy, CombatGeneralStatus.ONGOING);
    }

    public static CombatPersistence finishedCombat(
            Long userId,
            Long configId,
            UserStatsPersistence stats,
            EnemyPersistence enemy
    ) {
        return combat(userId, configId, stats, enemy, CombatGeneralStatus.USER_WON);
    }

    private static CombatPersistence combat(
            Long userId,
            Long configId,
            UserStatsPersistence stats,
            EnemyPersistence enemy,
            CombatGeneralStatus status
    ) {
        return CombatPersistence.builder()
                .userId(userId)
                .combatConfigId(configId)
                .enemyId(enemy.getId())

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
                .endsAt(Instant.now().plusSeconds(600))
                .combatStatus(status)
                .build();
    }

    // =========================================================================
    // COMBAT LOG
    // =========================================================================

    public static CombatLogPersistence combatLog(Long combatId, int turnNumber, CombatActorType actor) {
        return CombatLogPersistence.builder()
                .combatId(combatId)
                .turnNumber(turnNumber)
                .actor(actor)
                .combatActionType(CombatActionType.SKILL_USED)
                .skillName("Skill " + RANDOM.nextInt(1000))
                .createdAt(Instant.now())
                .build();
    }

    // =========================================================================
    // LAST SKILL
    // =========================================================================

    public static CombatLastSkillPersistence lastSkill(Long combatId, CombatActorType actor, Long skillId) {
        return CombatLastSkillPersistence.builder()
                .id(new CombatLastSkillKey(combatId, actor))
                .skillId(skillId)
                .build();
    }

    // =========================================================================
    // BLOCKED SKILL
    // =========================================================================

    public static CombatBlockedSkillPersistence blockedSkill(Long combatId, CombatActorType actor, Long skillId) {
        return CombatBlockedSkillPersistence.builder()
                .id(new CombatBlockedSkillKey(combatId, actor, skillId))
                .build();
    }

    // =========================================================================
    // BARK TRIGGER
    // =========================================================================

    public static CombatBarkTriggerPersistence barkTrigger(
            Long combatConfigId,
            BarkTriggerType type,
            Long conversationId
    ) {
        return CombatBarkTriggerPersistence.builder()
                .combatConfigId(combatConfigId)
                .type(type)
                .threshold(type == BarkTriggerType.SKILL_USED ? null : 50f)
                .skillId(type == BarkTriggerType.SKILL_USED ? 1L : null)
                .conversationId(conversationId)
                .dismissMode(BarkDismissMode.AUTO)
                .priority(0)
                .build();
    }

    public static CombatFiredBarkPersistence firedBark(Long combatId, Long triggerId) {
        return CombatFiredBarkPersistence.builder()
                .id(new CombatFiredBarkKey(combatId, triggerId))
                .build();
    }

    // =========================================================================
    // DTOs
    // =========================================================================

    public static CombatDTO combatDTO(Long id) {
        return CombatDTO.builder()
                .id(id)
                .combatConfigId(1L + RANDOM.nextInt(100))
                .turnNumber(1 + RANDOM.nextInt(10))
                .combatStatus(CombatGeneralStatus.ONGOING.name())
                .endsAt(Instant.now().plusSeconds(600).toEpochMilli())
                .enemyName("Enemy " + id)
                .enemySprite("sprite_" + id)
                .log(List.of())
                .build();
    }

    public static CombatTurnResultDTO combatTurnResultDTO(Long combatId) {
        return CombatTurnResultDTO.builder()
                .combatId(combatId)
                .turnNumber(1 + RANDOM.nextInt(10))
                .actions(List.of())
                .combatStatus(CombatGeneralStatus.ONGOING.name())
                .createdAt(Instant.now())
                .build();
    }
}
