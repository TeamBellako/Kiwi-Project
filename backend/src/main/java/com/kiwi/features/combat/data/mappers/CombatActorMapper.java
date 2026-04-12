package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.domain.CombatActiveStatusDomain;
import com.kiwi.features.combat.data.domain.CombatActorDomain;
import com.kiwi.features.combat.data.domain.ElementMultiplierDomain;
import com.kiwi.features.combat.data.domain.StatusResistanceDomain;
import com.kiwi.features.combat.data.dto.*;
import com.kiwi.features.combat.data.enums.CombatActionType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.persistence.EnemyPersistence;
import com.kiwi.features.combat.data.persistence.UserStatsPersistence;
import com.kiwi.features.skills.data.domain.SkillCombatDomain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CombatActorMapper {

    //------------------------------------------------------------------------------------------------------------------

    public static CombatActorDTO toDTO(int currentHP,
                                       CombatStatsDTO statsDTO,
                                       List<ElementMultiplierDTO> actorElements,
                                       List<StatusResistanceDTO> actorResistances,
                                       List<CombatActiveStatusDTO> activeStatus) {
        return CombatActorDTO.builder()
                .currentHp(currentHP)
                .stats(statsDTO)
                .elementalMultipliers(actorElements)
                .statusResistances(actorResistances)
                .activeStatus(activeStatus)
                .build();

    }

    //------------------------------------------------------------------------------------------------------------------

    private static CombatActorDomain buildActorDomainInternal(
            CombatActorType actorType,
            int currentHP,
            int maxHp,
            int patk,
            int matk,
            int pdef,
            int mdef,
            int acc,
            int eva,
            int lck,
            List<ElementMultiplierDomain> actorElements,
            List<StatusResistanceDomain> actorResistances,
            List<CombatActiveStatusDomain> activeStatus,
            List<SkillCombatDomain> skills,
            List<Long> skillsBlocked,
            Long lastSkillUsed
    ) {

        Map<Long, SkillCombatDomain> skillsMap =
                skills.stream()
                        .collect(Collectors.toMap(
                                SkillCombatDomain::getId,
                                skill -> skill
                        ));

        skillsBlocked.forEach(skillsMap::remove);

        Map<Long, ElementMultiplierDomain> elementsMap =
                actorElements.stream()
                        .collect(Collectors.toMap(
                                ElementMultiplierDomain::getElementId,
                                e -> e
                        ));

        Map<Long, StatusResistanceDomain> resistanceMap =
                actorResistances.stream()
                        .collect(Collectors.toMap(
                                StatusResistanceDomain::getStateId,
                                r -> r
                        ));

        return CombatActorDomain.builder()
                .type(actorType)
                .hp(currentHP)
                .maxHp(maxHp)
                .patk(patk)
                .matk(matk)
                .pdef(pdef)
                .mdef(mdef)
                .acc(acc)
                .eva(eva)
                .lck(lck)
                .elementMultipliers(elementsMap)
                .statusResistances(resistanceMap)
                .states(activeStatus)
                .skills(skillsMap)
                .lastSkillUsed(lastSkillUsed)
                .actionModifierType(CombatActionType.SKILL_USED)
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    public static CombatActorDomain buildActorDomain(
            CombatActorType actorType,
            int currentHP,
            UserStatsPersistence stats,
            List<ElementMultiplierDomain> elements,
            List<StatusResistanceDomain> resistances,
            List<CombatActiveStatusDomain> states,
            List<SkillCombatDomain> skills,
            List<Long> skillsBlocked,
            Long lastSkillUsed
    ) {
        return buildActorDomainInternal(
                actorType,
                currentHP,
                stats.getMaxHp(),
                stats.getPatk(),
                stats.getMatk(),
                stats.getPdef(),
                stats.getMdef(),
                stats.getAcc(),
                stats.getEva(),
                stats.getLck(),
                elements,
                resistances,
                states,
                skills,
                skillsBlocked,
                lastSkillUsed
        );
    }

    //------------------------------------------------------------------------------------------------------------------

    public static CombatActorDomain buildActorDomain(
            CombatActorType actorType,
            int currentHP,
            EnemyPersistence stats,
            List<ElementMultiplierDomain> elements,
            List<StatusResistanceDomain> resistances,
            List<CombatActiveStatusDomain> states,
            List<SkillCombatDomain> skills,
            List<Long> skillsBlocked,
            Long lastSkillUsed
    ) {
        return buildActorDomainInternal(
                actorType,
                currentHP,
                stats.getMaxHp(),
                stats.getPatk(),
                stats.getMatk(),
                stats.getPdef(),
                stats.getMdef(),
                stats.getAcc(),
                stats.getEva(),
                stats.getLck(),
                elements,
                resistances,
                states,
                skills,
                skillsBlocked,
                lastSkillUsed
        );
    }

    //------------------------------------------------------------------------------------------------------------------

}
