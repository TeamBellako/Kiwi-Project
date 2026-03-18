package com.kiwi.features.combat.engine;


import com.kiwi.features.combat.data.dto.ElementMultiplierDTO;
import com.kiwi.features.combat.data.dto.StatusResistanceDTO;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.persistence.CombatPersistence;
import com.kiwi.features.combat.data.persistence.EnemyPersistence;
import com.kiwi.features.combat.data.persistence.UserStatsPersistence;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CombatContextBuilder {

    public CombatContext build(
            CombatPersistence combat,
            UserStatsPersistence userStats,
            EnemyPersistence enemy,
            List<ElementMultiplierDTO> userElements,
            List<ElementMultiplierDTO> enemyElements,
            List<StatusResistanceDTO> userResistances,
            List<StatusResistanceDTO> enemyResistances,
            Map<Long, SkillRuntime> userSkills,
            Map<Long, SkillRuntime> enemySkills,
            Long userLastSkillUsed,
            Long enemyLastSkillUsed
            ) {

        ActorRuntime user =
                buildUserRuntime(combat, userStats, userElements, userResistances, userSkills, userLastSkillUsed);

        ActorRuntime enemyRuntime =
                buildEnemyRuntime(combat, enemy, enemyElements, enemyResistances, enemySkills, enemyLastSkillUsed);

        return new CombatContext(combat, user, enemyRuntime);
    }

    private ActorRuntime buildUserRuntime(
            CombatPersistence combat,
            UserStatsPersistence stats,
            List<ElementMultiplierDTO> elements,
            List<StatusResistanceDTO> resistances,
            Map<Long, SkillRuntime> skills,
            Long userLastSkillUsed
    ) {

        Map<Long, Float> elementMap =
                elements.stream()
                        .collect(Collectors.toMap(
                                ElementMultiplierDTO::getElementId,
                                ElementMultiplierDTO::getMultiplier
                        ));

        Map<Long, Float> resistanceMap =
                resistances.stream()
                        .collect(Collectors.toMap(
                                StatusResistanceDTO::getStateId,
                                StatusResistanceDTO::getResistance
                        ));
        return ActorRuntime.builder()
                .type(CombatActorType.USER)
                .hp(combat.getUserHp())
                .maxHp(stats.getMaxHp())
                .patk(stats.getPatk())
                .matk(stats.getMatk())
                .pdef(stats.getPdef())
                .mdef(stats.getMdef())
                .acc(stats.getAcc())
                .eva(stats.getEva())
                .lck(stats.getLck())
                .elementMultipliers(elementMap)
                .statusResistances(resistanceMap)
                .states(new ArrayList<>())
                .skills(skills)
                .lastSkillUsed(userLastSkillUsed)
                .build();
    }

    private ActorRuntime buildEnemyRuntime(
            CombatPersistence combat,
            EnemyPersistence enemy,
            List<ElementMultiplierDTO> elements,
            List<StatusResistanceDTO> resistances,
            Map<Long, SkillRuntime> skills,
            Long enemyLastSkillUsed
    ) {

        Map<Long, Float> elementMap =
                elements.stream()
                        .collect(Collectors.toMap(
                                ElementMultiplierDTO::getElementId,
                                ElementMultiplierDTO::getMultiplier
                        ));

        Map<Long, Float> resistanceMap =
                resistances.stream()
                        .collect(Collectors.toMap(
                                StatusResistanceDTO::getStateId,
                                StatusResistanceDTO::getResistance
                        ));

        return ActorRuntime.builder()
                .type(CombatActorType.ENEMY)
                .hp(combat.getEnemyHp())
                .maxHp(enemy.getMaxHp())
                .patk(enemy.getPatk())
                .matk(enemy.getMatk())
                .pdef(enemy.getPdef())
                .mdef(enemy.getMdef())
                .acc(enemy.getAcc())
                .eva(enemy.getEva())
                .lck(enemy.getLck())
                .elementMultipliers(elementMap)
                .statusResistances(resistanceMap)
                .states(new ArrayList<>())
                .skills(skills)
                .lastSkillUsed(enemyLastSkillUsed)
                .build();
    }
}