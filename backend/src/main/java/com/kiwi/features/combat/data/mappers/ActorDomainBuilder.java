package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.domain.*;
import com.kiwi.features.combat.data.enums.ActionType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.persistence.EnemyPersistence;
import com.kiwi.features.combat.data.persistence.UserStatsPersistence;
import com.kiwi.features.skills.data.domain.SkillCombatDomain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActorDomainBuilder {

    public static ActorDomain buildActorRuntime(
            CombatActorType actorType,
            int currentHP,
            UserStatsPersistence actorStats,
            List<ElementMultiplierDomain> actorElements,
            List<StatusResistanceDomain> actorResistances,
            List<CombatActiveStatusDomain> activeStatus,
            List<SkillCombatDomain> skills,
            List<Long> userSkillsBlocked,
            Long lastSkillUsed
    ) {

        Map<Long, SkillCombatDomain> skillsMap =
                skills.stream()
                        .collect(Collectors.toMap(
                                SkillCombatDomain::getId,
                                skill -> skill
                        ));
        userSkillsBlocked.forEach(skillsMap::remove);

        Map<Long, Float> elementsMap =
                actorElements.stream()
                        .collect(Collectors.toMap(
                                ElementMultiplierDomain::getElementId,
                                ElementMultiplierDomain::getMultiplier
                        ));

        Map<Long, Float> resistanceMap =
                actorResistances.stream()
                        .collect(Collectors.toMap(
                                StatusResistanceDomain::getStateId,
                                StatusResistanceDomain::getResistance
                        ));

        return ActorDomain.builder()
                .type(actorType)
                .hp(currentHP)
                .maxHp(actorStats.getMaxHp())
                .patk(actorStats.getPatk())
                .matk(actorStats.getMatk())
                .pdef(actorStats.getPdef())
                .mdef(actorStats.getMdef())
                .acc(actorStats.getAcc())
                .eva(actorStats.getEva())
                .lck(actorStats.getLck())
                .elementMultipliers(elementsMap)
                .statusResistances(resistanceMap)
                .states(activeStatus)
                .skills(skillsMap)
                .lastSkillUsed(lastSkillUsed)
                .actionModifierType(ActionType.SKILL_USED)
                .build();
    }

    public static ActorDomain buildActorRuntime(
            CombatActorType actorType,
            int currentHP,
            EnemyPersistence enemyStats,
            List<ElementMultiplierDomain> actorElements,
            List<StatusResistanceDomain> actorResistances,
            List<CombatActiveStatusDomain> activeStatus,
            List<SkillCombatDomain> skills,
            List<Long> userSkillsBlocked,
            Long lastSkillUsed
    ) {

        Map<Long, SkillCombatDomain> skillsMap =
                skills.stream()
                        .collect(Collectors.toMap(
                                SkillCombatDomain::getId,
                                skill -> skill
                        ));
        userSkillsBlocked.forEach(skillsMap::remove);

        Map<Long, Float> elementsMap =
                actorElements.stream()
                        .collect(Collectors.toMap(
                                ElementMultiplierDomain::getElementId,
                                ElementMultiplierDomain::getMultiplier
                        ));

        Map<Long, Float> resistanceMap =
                actorResistances.stream()
                        .collect(Collectors.toMap(
                                StatusResistanceDomain::getStateId,
                                StatusResistanceDomain::getResistance
                        ));

        return ActorDomain.builder()
                .type(actorType)
                .hp(currentHP)
                .maxHp(enemyStats.getMaxHp())
                .patk(enemyStats.getPatk())
                .matk(enemyStats.getMatk())
                .pdef(enemyStats.getPdef())
                .mdef(enemyStats.getMdef())
                .acc(enemyStats.getAcc())
                .eva(enemyStats.getEva())
                .lck(enemyStats.getLck())
                .elementMultipliers(elementsMap)
                .statusResistances(resistanceMap)
                .states(activeStatus)
                .skills(skillsMap)
                .lastSkillUsed(lastSkillUsed)
                .actionModifierType(ActionType.SKILL_USED)
                .build();
    }
}
