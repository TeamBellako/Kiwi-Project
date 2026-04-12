package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.domain.CombatActorDomain;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.mappers.CombatActorMapper;
import com.kiwi.features.combat.data.persistence.*;
import com.kiwi.features.combat.repositories.EnemyRepository;
import com.kiwi.features.combat.repositories.UserStatsRepository;
import com.kiwi.features.skills.controllers.SkillService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CombatActorBuilderService {

    private final CombatDataService combatDataService;
    private final CombatStateService combatStateService;
    private final SkillService skillService;
    private final CombatBlockedSkillService blockedSkillService;
    private final CombatLastSkillService lastSkillService;
    private final UserStatsRepository userStatsRepository;
    private final EnemyRepository enemyRepository;

    //------------------------------------------------------------------------------------------------------------------

    public CombatActorBuilderService(
            CombatDataService combatDataService,
            CombatStateService combatStateService,
            SkillService skillService,
            CombatBlockedSkillService blockedSkillService,
            CombatLastSkillService lastSkillService,
            UserStatsRepository userStatsRepository,
            EnemyRepository enemyRepository
    ) {
        this.combatDataService = combatDataService;
        this.combatStateService = combatStateService;
        this.skillService = skillService;
        this.blockedSkillService = blockedSkillService;
        this.lastSkillService = lastSkillService;
        this.userStatsRepository = userStatsRepository;
        this.enemyRepository = enemyRepository;
    }

    //------------------------------------------------------------------------------------------------------------------

    public CombatActorDomain buildUser(Long userId, CombatPersistence combat) {

        UserStatsPersistence stats = userStatsRepository.findById(userId).orElseThrow();

        Map<Long, CombatElementPersistence> elements = combatDataService.loadElementsMap();
        Map<Long, CombatStatePersistence> states = combatDataService.loadStatesMap();

        return CombatActorMapper.buildActorDomain(
                CombatActorType.USER,
                combat.getUserHp(),
                stats,
                combatDataService.loadUserElements(userId, elements),
                combatDataService.loadUserResistances(userId, states),
                combatStateService.getActiveStatus(combat.getId(), CombatActorType.USER),
                skillService.getCombatSkillsForUser(userId),
                blockedSkillService.getBlockedSkills(combat.getId(), CombatActorType.USER),
                lastSkillService.getLastSkill(combat.getId(), CombatActorType.USER)
        );
    }

    //------------------------------------------------------------------------------------------------------------------

    public CombatActorDomain buildEnemy(Long enemyId, CombatPersistence combat) {

        EnemyPersistence enemy = enemyRepository.findById(enemyId).orElseThrow();

        Map<Long, CombatElementPersistence> elements = combatDataService.loadElementsMap();
        Map<Long, CombatStatePersistence> states = combatDataService.loadStatesMap();

        return CombatActorMapper.buildActorDomain(
                CombatActorType.ENEMY,
                combat.getEnemyHp(),
                enemy,
                combatDataService.loadEnemyElements(enemyId, elements),
                combatDataService.loadEnemyResistances(enemyId, states),
                combatStateService.getActiveStatus(combat.getId(), CombatActorType.ENEMY),
                skillService.getCombatSkillsForEnemy(enemyId),
                blockedSkillService.getBlockedSkills(combat.getId(), CombatActorType.ENEMY),
                lastSkillService.getLastSkill(combat.getId(), CombatActorType.ENEMY)
        );
    }

    //------------------------------------------------------------------------------------------------------------------

}
