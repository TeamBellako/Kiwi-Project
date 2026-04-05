package com.kiwi.features.combat.engine;


import com.kiwi.features.combat.data.domain.CombatStatusAppliedDomain;
import com.kiwi.features.combat.data.domain.ActorDomain;
import com.kiwi.features.skills.data.domain.SkillCombatDomain;
import com.kiwi.features.combat.data.dto.*;
import com.kiwi.features.combat.data.enums.ActionType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.persistence.CombatPersistence;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CombatContextBuilder {

    public CombatContext build(
            CombatPersistence combat,
            CombatActorDTO userDTO,
            CombatActorDTO enemyDTO,
            List<SkillCombatDomain> userSkills,
            List<SkillCombatDomain> enemySkills,
            Long userLastSkillUsed,
            Long enemyLastSkillUsed
            ) {

        ActorDomain user =
                buildActorRuntime(CombatActorType.USER, combat, userDTO, userSkills, userLastSkillUsed);

        ActorDomain enemyRuntime =
                buildActorRuntime(CombatActorType.ENEMY, combat, enemyDTO, enemySkills, enemyLastSkillUsed);

        return new CombatContext(combat, user, enemyRuntime);
    }

    private ActorDomain buildActorRuntime(
            CombatActorType actorType,
            CombatPersistence combat,
            CombatActorDTO combatActorDTO,
            List<SkillCombatDomain> skills,
            Long lastSkillUsed
    ) {
        StatsDTO stats = combatActorDTO.getStats();

        Map<Long, Float> elementsMap =
                combatActorDTO.getElementalMultipliers().stream()
                        .collect(Collectors.toMap(
                                ElementMultiplierDTO::getElementId,
                                ElementMultiplierDTO::getMultiplier
                        ));

        Map<Long, Float> resistanceMap =
                combatActorDTO.getStatusResistances().stream()
                        .collect(Collectors.toMap(
                                StatusResistanceDTO::getStateId,
                                StatusResistanceDTO::getResistance
                        ));

        return ActorDomain.builder()
                .type(actorType)
                .hp(combat.getUserHp())
                .maxHp(stats.getMaxHp())
                .patk(stats.getPatk())
                .matk(stats.getMatk())
                .pdef(stats.getPdef())
                .mdef(stats.getMdef())
                .acc(stats.getAcc())
                .eva(stats.getEva())
                .lck(stats.getLck())
                .elementMultipliers(elementsMap)
                .statusResistances(resistanceMap)
                .states(buildActiveStates(combatActorDTO.getStatusApplied()))
                .skills(skills)
                .lastSkillUsed(lastSkillUsed)
                .actionModifierType(ActionType.SKILL_USED)
                .build();
    }

    //-----------------------------------------------------------------------------------------------------------------

    List<CombatStatusAppliedDomain> buildActiveStates(List<CombatStatusAppliedDTO> statusAppliedList){

        List<CombatStatusAppliedDomain> activeStates = new ArrayList<>();

        for (CombatStatusAppliedDTO statusApplied : statusAppliedList){

            CombatStatusAppliedDomain activeState = CombatStatusAppliedDomain.builder()
                    .stateId(statusApplied.getStateId())
                    .name(statusApplied.getName())
                    .remainingTurns(statusApplied.getRemainingTurns())
                    .value(statusApplied.getValue())
                    .build();

            activeStates.add(activeState);
        }

        return activeStates;
    }

}