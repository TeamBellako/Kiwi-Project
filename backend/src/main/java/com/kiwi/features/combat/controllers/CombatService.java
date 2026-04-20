package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.mappers.*;
import com.kiwi.features.combat.data.persistence.*;
import com.kiwi.features.combat.repositories.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.Optional;


@Service
public class CombatService {

    private final CombatRepository combatRepository;
    private final CombatConfigRepository combatConfigRepository;
    private final EnemyRepository enemyRepository;
    private final UserStatsRepository userStatsRepository;

    private final CombatLogService combatLogService;
    private final CombatLastSkillService lastSkillService;
    private final CombatBlockedSkillService blockedSkillService;

    //------------------------------------------------------------------------------------------------------------------

    public CombatService(
            CombatRepository combatRepository,
            CombatConfigRepository combatConfigRepository,
            EnemyRepository enemyRepository,
            UserStatsRepository userStatsRepository,
            CombatLogService combatLogService,
            CombatLastSkillService lastSkillService,
            CombatBlockedSkillService blockedSkillService
    ) {
        this.combatRepository = combatRepository;
        this.combatConfigRepository = combatConfigRepository;
        this.enemyRepository = enemyRepository;
        this.userStatsRepository = userStatsRepository;
        this.combatLogService = combatLogService;
        this.lastSkillService = lastSkillService;
        this.blockedSkillService = blockedSkillService;
    }

    //------------------------------------------------------------------------------------------------------------------

    public Optional<CombatPersistence> findCombat(Long combatId) {

        return combatRepository.findById(combatId);
    }

    //------------------------------------------------------------------------------------------------------------------

    public CombatPersistence startOrResume(Long userId, Long configId) {

        Optional<CombatPersistence> existing =
                combatRepository.findByUserIdAndCombatConfigId(userId, configId);

        if (existing.isPresent()) {
            return existing.get();
        }

        UserStatsPersistence stats = userStatsRepository.findById(userId).orElseThrow();
        CombatConfigPersistence config = combatConfigRepository.findById(configId).orElseThrow();
        EnemyPersistence enemy = enemyRepository.findById(config.getEnemyId()).orElseThrow();

        Instant endsAt = Instant.now().plus(config.getTimeLimit(), ChronoUnit.MINUTES);

        CombatPersistence combat = CombatMapper.toNewCombat(
                userId,
                configId,
                stats,
                enemy,
                endsAt
        );

        return combatRepository.save(combat);
    }

    //------------------------------------------------------------------------------------------------------------------

    public void cleanDatabase(Long combatId) {

        combatLogService.deleteCombatLog(combatId);
        lastSkillService.deleteByCombatId(combatId);
        blockedSkillService.deleteByCombatId(combatId);
    }

    //------------------------------------------------------------------------------------------------------------------

}