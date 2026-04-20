package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.domain.*;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.mappers.CombatActorMapper;
import com.kiwi.features.combat.data.mappers.StatsMapper;
import com.kiwi.features.combat.data.persistence.CombatElementPersistence;
import com.kiwi.features.combat.data.persistence.CombatPersistence;
import com.kiwi.features.combat.data.persistence.CombatStatePersistence;
import com.kiwi.features.skills.controllers.SkillService;
import com.kiwi.features.skills.data.domain.SkillCombatDomain;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CombatActorBuilderService {

    private final CombatStaticDataService combatStaticDataService;
    private final CombatStatesService combatStatesService;
    private final CombatBlockedSkillService blockedSkillService;
    private final CombatLastSkillService lastSkillService;
    private final SkillService skillService;

    public CombatActorBuilderService(
            CombatStaticDataService combatStaticDataService,
            CombatStatesService combatStatesService,
            CombatBlockedSkillService blockedSkillService,
            CombatLastSkillService lastSkillService,
            SkillService skillService
    ) {
        this.combatStaticDataService = combatStaticDataService;
        this.combatStatesService = combatStatesService;
        this.blockedSkillService = blockedSkillService;
        this.lastSkillService = lastSkillService;
        this.skillService = skillService;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public Map<CombatActorType, CombatActorDomain> buildActors(CombatPersistence combat) {

        Map<Long, CombatElementPersistence> elements =
                combatStaticDataService.loadElementsMap();

        Map<Long, CombatStatePersistence> states =
                combatStatesService.loadStatesMap();

        Map<CombatActorType, List<CombatActiveStatusDomain>> activeStatuses =
                combatStatesService.getActiveStatuses(combat.getId());

        Map<CombatActorType, List<Long>> blockedSkills =
                blockedSkillService.getBlockedSkills(combat.getId());

        Map<CombatActorType, Long> lastSkills =
                lastSkillService.getLastSkills(combat.getId());

        // USER
        CombatActorDomain user = buildActor(
                combat,
                CombatActorType.USER,
                combat.getUserId(),
                elements,
                states,
                activeStatuses,
                blockedSkills,
                lastSkills
        );

        // ENEMY
        CombatActorDomain enemy = buildActor(
                combat,
                CombatActorType.ENEMY,
                combat.getEnemyId(),
                elements,
                states,
                activeStatuses,
                blockedSkills,
                lastSkills
        );

        return Map.of(
                CombatActorType.USER, user,
                CombatActorType.ENEMY, enemy
        );
    }

    // ----------------------------------------------------------------------------------------------------------------

    private CombatActorDomain buildActor(
            CombatPersistence combat,
            CombatActorType actorType,
            Long actorId,
            Map<Long, CombatElementPersistence> elements,
            Map<Long, CombatStatePersistence> states,
            Map<CombatActorType, List<CombatActiveStatusDomain>> activeStatusesMap,
            Map<CombatActorType, List<Long>> blockedSkillsMap,
            Map<CombatActorType, Long> lastSkillsMap
    ) {

        List<ElementMultiplierDomain> elementsMult =
                actorType == CombatActorType.USER
                        ? combatStaticDataService.loadUserElements(actorId, elements)
                        : combatStaticDataService.loadEnemyElements(actorId, elements);

        List<StatusResistanceDomain> resistances =
                actorType == CombatActorType.USER
                        ? combatStaticDataService.loadUserResistances(actorId, states)
                        : combatStaticDataService.loadEnemyResistances(actorId, states);

        List<CombatActiveStatusDomain> activeStatuses =
                combatStatesService.getActiveStatusesForActor(activeStatusesMap, actorType);

        List<Long> blockedSkills =
                blockedSkillService.getBlockedSkillsForActor(blockedSkillsMap, actorType);

        Long lastSkillUsed =
                lastSkillService.getLastSkillForActor(lastSkillsMap, actorType);

        StatsDomain stats =
                actorType == CombatActorType.USER
                        ? StatsMapper.toDomainUser(combat)
                        : StatsMapper.toDomainEnemy(combat);

        List<SkillCombatDomain> skills =
                actorType == CombatActorType.USER
                        ? skillService.getCombatSkillsForUser(actorId)
                        : skillService.getCombatSkillsForEnemy(actorId);

        return CombatActorMapper.buildActorDomain(
                actorType,
                stats,
                elementsMult,
                resistances,
                activeStatuses,
                skills,
                blockedSkills,
                lastSkillUsed
        );
    }
}