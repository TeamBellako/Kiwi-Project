package com.kiwi.combat;

import com.kiwi.features.combat.controllers.*;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.data.persistence.*;
import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.kiwi.combat.CombatTestFactory.*;
import static org.junit.jupiter.api.Assertions.*;

public class CombatServiceTests {

    private final CombatTestRepositoryInMemory combatRepo =
            new CombatTestRepositoryInMemory();

    private final CombatConfigTestRepositoryInMemory configRepo =
            new CombatConfigTestRepositoryInMemory();

    private final EnemyTestRepositoryInMemory enemyRepo =
            new EnemyTestRepositoryInMemory();

    private final UserStatsTestRepositoryInMemory userStatsRepo =
            new UserStatsTestRepositoryInMemory();

    private final CombatLogTestRepositoryInMemory combatLogRepo =
            new CombatLogTestRepositoryInMemory();

    private final CombatLastSkillTestRepositoryInMemory lastSkillRepo =
            new CombatLastSkillTestRepositoryInMemory();

    private final CombatBlockedSkillTestRepositoryInMemory blockedSkillRepo =
            new CombatBlockedSkillTestRepositoryInMemory();

    private final CombatLogService combatLogService =
            new CombatLogService(combatLogRepo);

    private final CombatLastSkillService lastSkillService =
            new CombatLastSkillService(lastSkillRepo);

    private final CombatBlockedSkillService blockedSkillService =
            new CombatBlockedSkillService(blockedSkillRepo);

    private final CombatBarkTriggerTestRepositoryInMemory barkTriggerRepo =
            new CombatBarkTriggerTestRepositoryInMemory();

    private final CombatFiredBarkTestRepositoryInMemory firedBarkRepo =
            new CombatFiredBarkTestRepositoryInMemory();

    private final CombatBarkService barkService =
            new CombatBarkService(barkTriggerRepo, firedBarkRepo);

    private final CombatService service = new CombatService(
            combatRepo,
            configRepo,
            enemyRepo,
            userStatsRepo,
            combatLogService,
            lastSkillService,
            blockedSkillService,
            barkService
    );

    private final Long userId = 1L;

    // ============================================================================================
    // FIND COMBAT
    // ============================================================================================

    @Test
    public void findCombat_returnsCombatWhenExists() {

        EnemyPersistence enemy = enemyRepo.saveAndFlush(enemy(null));
        UserStatsPersistence stats = userStatsRepo.saveAndFlush(userStats(userId));
        CombatPersistence combat = combatRepo.saveAndFlush(
                ongoingCombat(userId, 1L, stats, enemy)
        );

        Optional<CombatPersistence> result = service.findCombat(combat.getId());

        assertTrue(result.isPresent());
        assertEquals(combat.getId(), result.get().getId());
    }

    @Test
    public void findCombat_returnsEmptyWhenMissing() {

        Optional<CombatPersistence> result = service.findCombat(999L);

        assertTrue(result.isEmpty());
    }

    // ============================================================================================
    // FIND ACTIVE COMBAT
    // ============================================================================================

    @Test
    public void findActiveCombat_returnsOnlyOngoing() {

        EnemyPersistence enemy = enemyRepo.saveAndFlush(enemy(null));
        UserStatsPersistence stats = userStatsRepo.saveAndFlush(userStats(userId));

        combatRepo.saveAndFlush(finishedCombat(userId, 1L, stats, enemy));
        CombatPersistence ongoing = combatRepo.saveAndFlush(
                ongoingCombat(userId, 2L, stats, enemy)
        );

        Optional<CombatPersistence> result = service.findActiveCombat(userId);

        assertTrue(result.isPresent());
        assertEquals(ongoing.getId(), result.get().getId());
        assertEquals(CombatGeneralStatus.ONGOING, result.get().getCombatStatus());
    }

    @Test
    public void findActiveCombat_returnsEmptyWhenNoOngoing() {

        EnemyPersistence enemy = enemyRepo.saveAndFlush(enemy(null));
        UserStatsPersistence stats = userStatsRepo.saveAndFlush(userStats(userId));

        combatRepo.saveAndFlush(finishedCombat(userId, 1L, stats, enemy));

        Optional<CombatPersistence> result = service.findActiveCombat(userId);

        assertTrue(result.isEmpty());
    }

    // ============================================================================================
    // START OR RESUME
    // ============================================================================================

    @Test
    public void startOrResume_createsNewCombatWhenNoneExists() {

        UserStatsPersistence stats = userStatsRepo.saveAndFlush(userStats(userId));
        EnemyPersistence enemy = enemyRepo.saveAndFlush(enemy(null));
        CombatConfigPersistence config = configRepo.saveAndFlush(combatConfig(null, enemy.getId()));

        CombatPersistence result = service.startOrResume(userId, config.getId());

        assertNotNull(result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals(config.getId(), result.getCombatConfigId());
        assertEquals(enemy.getId(), result.getEnemyId());
        assertEquals(CombatGeneralStatus.ONGOING, result.getCombatStatus());

        // user/enemy stats should be snapshotted from sources
        assertEquals(stats.getMaxHp(), result.getUserMaxHp());
        assertEquals(stats.getMaxHp(), result.getUserHp());
        assertEquals(enemy.getMaxHp(), result.getEnemyMaxHp());
        assertEquals(enemy.getMaxHp(), result.getEnemyHp());
        assertEquals(1, result.getTurnNumber());
        assertNotNull(result.getEndsAt());
    }

    @Test
    public void startOrResume_returnsExistingOngoingCombat() {

        UserStatsPersistence stats = userStatsRepo.saveAndFlush(userStats(userId));
        EnemyPersistence enemy = enemyRepo.saveAndFlush(enemy(null));
        CombatConfigPersistence config = configRepo.saveAndFlush(combatConfig(null, enemy.getId()));

        CombatPersistence existing = combatRepo.saveAndFlush(
                ongoingCombat(userId, config.getId(), stats, enemy)
        );

        CombatPersistence result = service.startOrResume(userId, config.getId());

        assertEquals(existing.getId(), result.getId());
        assertEquals(1, combatRepo.count());
    }

    @Test
    public void startOrResume_replacesFinishedCombat() {

        UserStatsPersistence stats = userStatsRepo.saveAndFlush(userStats(userId));
        EnemyPersistence enemy = enemyRepo.saveAndFlush(enemy(null));
        CombatConfigPersistence config = configRepo.saveAndFlush(combatConfig(null, enemy.getId()));

        CombatPersistence finished = combatRepo.saveAndFlush(
                finishedCombat(userId, config.getId(), stats, enemy)
        );

        CombatPersistence result = service.startOrResume(userId, config.getId());

        assertNotEquals(finished.getId(), result.getId());
        assertEquals(CombatGeneralStatus.ONGOING, result.getCombatStatus());
        assertEquals(1, combatRepo.count());
        assertTrue(combatRepo.findById(finished.getId()).isEmpty());
    }

    @Test(expected = NoSuchElementException.class)
    public void startOrResume_failsWhenStatsMissing() {

        EnemyPersistence enemy = enemyRepo.saveAndFlush(enemy(null));
        CombatConfigPersistence config = configRepo.saveAndFlush(combatConfig(null, enemy.getId()));

        service.startOrResume(userId, config.getId());
    }

    @Test(expected = NoSuchElementException.class)
    public void startOrResume_failsWhenConfigMissing() {

        userStatsRepo.saveAndFlush(userStats(userId));

        service.startOrResume(userId, 999L);
    }

    @Test(expected = NoSuchElementException.class)
    public void startOrResume_failsWhenEnemyMissing() {

        userStatsRepo.saveAndFlush(userStats(userId));
        CombatConfigPersistence config = configRepo.saveAndFlush(combatConfig(null, 999L));

        service.startOrResume(userId, config.getId());
    }

    // ============================================================================================
    // CLEAN DATABASE
    // ============================================================================================

    @Test
    public void cleanDatabase_removesLogsLastSkillsAndBlockedSkills() {

        Long combatId = 42L;
        Long otherCombatId = 43L;

        combatLogRepo.save(combatLog(combatId, 1, CombatActorType.USER));
        combatLogRepo.save(combatLog(combatId, 2, CombatActorType.ENEMY));
        combatLogRepo.save(combatLog(otherCombatId, 1, CombatActorType.USER));

        lastSkillRepo.save(lastSkill(combatId, CombatActorType.USER, 100L));
        lastSkillRepo.save(lastSkill(combatId, CombatActorType.ENEMY, 200L));
        lastSkillRepo.save(lastSkill(otherCombatId, CombatActorType.USER, 300L));

        blockedSkillRepo.save(blockedSkill(combatId, CombatActorType.USER, 100L));
        blockedSkillRepo.save(blockedSkill(combatId, CombatActorType.ENEMY, 200L));
        blockedSkillRepo.save(blockedSkill(otherCombatId, CombatActorType.USER, 300L));

        service.cleanDatabase(combatId);

        assertTrue(combatLogRepo.findByCombatIdOrderByIdAsc(combatId).isEmpty());
        assertTrue(lastSkillRepo.findById_CombatId(combatId).isEmpty());
        assertTrue(blockedSkillRepo.findById_CombatId(combatId).isEmpty());

        // unrelated combat untouched
        assertEquals(1, combatLogRepo.findByCombatIdOrderByIdAsc(otherCombatId).size());
        assertEquals(1, lastSkillRepo.findById_CombatId(otherCombatId).size());
        assertEquals(1, blockedSkillRepo.findById_CombatId(otherCombatId).size());
    }

    // ============================================================================================
    // RESET STATS TO ORIGINAL CONFIG
    // ============================================================================================

    @Test
    public void resetStatsToOriginalConfig_restoresStatsFromSources() {

        UserStatsPersistence stats = userStatsRepo.saveAndFlush(userStats(userId));
        EnemyPersistence enemy = enemyRepo.saveAndFlush(enemy(null));

        CombatPersistence combat = combatRepo.saveAndFlush(
                ongoingCombat(userId, 1L, stats, enemy)
        );

        // tweak combat stats so we can verify restoration
        combat.setUserHp(1);
        combat.setUserPatk(0);
        combat.setEnemyHp(1);
        combat.setEnemyMatk(0);
        combatRepo.saveAndFlush(combat);

        service.resetStatsToOriginalConfig(combat);

        CombatPersistence updated = combatRepo.findById(combat.getId()).orElseThrow();

        assertEquals(stats.getMaxHp(), updated.getUserHp());
        assertEquals(stats.getMaxHp(), updated.getUserMaxHp());
        assertEquals(stats.getPatk(), updated.getUserPatk());
        assertEquals(stats.getMatk(), updated.getUserMatk());
        assertEquals(stats.getPdef(), updated.getUserPdef());
        assertEquals(stats.getMdef(), updated.getUserMdef());
        assertEquals(stats.getAcc(), updated.getUserAcc());
        assertEquals(stats.getEva(), updated.getUserEva());
        assertEquals(stats.getLck(), updated.getUserLck());

        assertEquals(enemy.getMaxHp(), updated.getEnemyHp());
        assertEquals(enemy.getMaxHp(), updated.getEnemyMaxHp());
        assertEquals(enemy.getPatk(), updated.getEnemyPatk());
        assertEquals(enemy.getMatk(), updated.getEnemyMatk());
        assertEquals(enemy.getPdef(), updated.getEnemyPdef());
        assertEquals(enemy.getMdef(), updated.getEnemyMdef());
        assertEquals(enemy.getAcc(), updated.getEnemyAcc());
        assertEquals(enemy.getEva(), updated.getEnemyEva());
        assertEquals(enemy.getLck(), updated.getEnemyLck());
    }

    @Test(expected = NoSuchElementException.class)
    public void resetStatsToOriginalConfig_failsWhenStatsMissing() {

        EnemyPersistence enemy = enemyRepo.saveAndFlush(enemy(null));
        UserStatsPersistence stats = userStats(userId);
        CombatPersistence combat = combatRepo.saveAndFlush(
                ongoingCombat(userId, 1L, stats, enemy)
        );

        service.resetStatsToOriginalConfig(combat);
    }

    @Test(expected = NoSuchElementException.class)
    public void resetStatsToOriginalConfig_failsWhenEnemyMissing() {

        UserStatsPersistence stats = userStatsRepo.saveAndFlush(userStats(userId));
        EnemyPersistence enemy = enemy(999L);
        CombatPersistence combat = combatRepo.saveAndFlush(
                ongoingCombat(userId, 1L, stats, enemy)
        );

        service.resetStatsToOriginalConfig(combat);
    }
}
