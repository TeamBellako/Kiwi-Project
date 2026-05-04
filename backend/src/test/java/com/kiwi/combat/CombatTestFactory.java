package com.kiwi.combat;

import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.enums.CombatActionType;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.data.persistence.*;

import java.time.Instant;
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
                .userPatk(stats.getPatk())
                .userMatk(stats.getMatk())
                .userPdef(stats.getPdef())
                .userMdef(stats.getMdef())
                .userAcc(stats.getAcc())
                .userEva(stats.getEva())
                .userLck(stats.getLck())

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
}
