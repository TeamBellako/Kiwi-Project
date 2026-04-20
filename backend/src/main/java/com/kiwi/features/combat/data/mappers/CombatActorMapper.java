package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.domain.*;
import com.kiwi.features.combat.data.dto.*;
import com.kiwi.features.combat.data.enums.CombatActionType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.persistence.EnemyPersistence;
import com.kiwi.features.combat.data.persistence.UserStatsPersistence;
import com.kiwi.features.skills.data.domain.SkillCombatDomain;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CombatActorMapper {

    //------------------------------------------------------------------------------------------------------------------

    public static CombatActorDTO toDTO(CombatStatsDTO statsDTO,
                                       List<ElementMultiplierDTO> actorElements,
                                       List<StatusResistanceDTO> actorResistances,
                                       List<CombatActiveStatusDTO> activeStatus) {
        return CombatActorDTO.builder()
                .stats(statsDTO)
                .elementalMultipliers(actorElements)
                .statusResistances(actorResistances)
                .activeStatus(activeStatus)
                .build();

    }

    //------------------------------------------------------------------------------------------------------------------

    public static CombatActorDomain buildActorDomain(
            CombatActorType actorType,
            StatsDomain stats,
            List<ElementMultiplierDomain> elements,
            List<StatusResistanceDomain> resistances,
            List<CombatActiveStatusDomain> statuses,
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
                elements.stream()
                        .collect(Collectors.toMap(
                                ElementMultiplierDomain::getElementId,
                                Function.identity()
                        ));

        Map<Long, StatusResistanceDomain> resistanceMap =
                resistances.stream()
                        .collect(Collectors.toMap(
                                StatusResistanceDomain::getStateId,
                                Function.identity()
                        ));

        return CombatActorDomain.builder()
                .type(actorType)
                .stats(stats)
                .elementMultipliers(elementsMap)
                .statusResistances(resistanceMap)
                .activeStatuses(statuses)
                .skills(skillsMap)
                .lastSkillUsed(lastSkillUsed)
                .actionModifierType(CombatActionType.SKILL_USED)
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

}
