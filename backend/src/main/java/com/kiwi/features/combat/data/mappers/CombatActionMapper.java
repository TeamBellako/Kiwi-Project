package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.domain.CombatActionDomain;
import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.persistence.CombatLogPersistence;

import java.time.Instant;
import java.util.List;

public class CombatActionMapper {


    //------------------------------------------------------------------------------------------------------------------

    public static List<CombatLogPersistence> toCombatLogPersistence(
            CombatActionDomain action,
            Long combatId,
            int turnNumber
    ) {

        return switch (action.getActionType()) {

            case SKILL_USED -> mapSkillUsed(action, combatId, turnNumber);

            case ACTOR_BLOCKED_BY_STATE,
                 SKILL_REPEAT_BY_STATE,
                 STATUS_TURN_REDUCED,
                 STATUS_FINISHED -> List.of(
                    baseCombatLogPersistenceBuilder(action, combatId, turnNumber)
                            .stateName(action.getState()  != null ? action.getState().getName() : null)
                            .stateId(action.getState()  != null ? action.getState().getStateId() : null)
                            .build()
            );

            case ACTOR_DAMAGED_BY_STATE -> List.of(
                    baseCombatLogPersistenceBuilder(action, combatId, turnNumber)
                            .stateName(action.getState()  != null ? action.getState().getName() : null)
                            .stateId(action.getState()  != null ? action.getState().getStateId() : null)
                            .value(action.getStateEffectValue())
                            .build()
            );

            case BLOCKED_SKILLS_BY_STATE,
                 RELEASED_SKILLS_BY_STATE -> List.of(
                    baseCombatLogPersistenceBuilder(action, combatId, turnNumber)
                            .stateName(action.getState()  != null ? action.getState().getName() : null)
                            .stateId(action.getState()  != null ? action.getState().getStateId() : null)
                            .blockedSkills(
                                    CombatActionMapper.blockedSkillsToString(action.getBlockedSkills())
                            )
                            .build()
            );

            case SKIP, TIMEOUT -> List.of(
                    baseCombatLogPersistenceBuilder(action, combatId, turnNumber).build()
            );
        };
    }

    //------------------------------------------------------------------------------------------------------------------

    private static List<CombatLogPersistence> mapSkillUsed(
            CombatActionDomain action,
            Long combatId,
            int turnNumber
    ) {

        if (action.getSkillEffectsResults() == null || action.getSkillEffectsResults().isEmpty()) {
            return List.of(
                    baseCombatLogPersistenceBuilder(action, combatId, turnNumber)
                            .skillName(action.getSkillName())
                            .build()
            );
        }

        return action.getSkillEffectsResults().stream()
                .map(effect -> {
                    CombatLogPersistence.CombatLogPersistenceBuilder builder =
                            baseCombatLogPersistenceBuilder(action, combatId, turnNumber)
                                    .skillName(action.getSkillName())
                                    .effectType(effect.getTypeResult())
                                    .target(effect.getTarget())
                                    .value(effect.getValue())
                                    .critic(effect.isCritic());

                    if (effect.getAppliedStatus() != null) {
                        builder
                                .stateId(effect.getAppliedStatus().getStateId())
                                .statusDuration(effect.getAppliedStatus().getRemainingTurns());
                    }

                    return builder.build();
                })
                .toList();
    }

    //------------------------------------------------------------------------------------------------------------------

    private static CombatLogPersistence.CombatLogPersistenceBuilder baseCombatLogPersistenceBuilder(
            CombatActionDomain action,
            Long combatId,
            int turnNumber
    ) {
        return CombatLogPersistence.builder()
                .combatId(combatId)
                .turnNumber(turnNumber)
                .actor(action.getActor() != null ? action.getActor() : null)
                .combatActionType(action.getActionType() != null ? action.getActionType() : null)
                .createdAt(Instant.now());
    }

    //------------------------------------------------------------------------------------------------------------------

    public static String blockedSkillsToString(List<Long> list) {
        return list == null ? null :
                list.stream()
                        .map(String::valueOf)
                        .reduce((a, b) -> a + "," + b)
                        .orElse(null);
    }

    //------------------------------------------------------------------------------------------------------------------

    public static CombatActionDTO toDTO(CombatActionDomain action) {

        return CombatActionDTO.builder()
                .actor(action.getActor() != null ? action.getActor().name() : null)
                .actionType(action.getActionType() != null ? action.getActionType().name() : null)
                .skillName(action.getSkillName())
                .stateEffectValue(action.getStateEffectValue())
                .blockedSkills(action.getBlockedSkills())
                .stateName(action.getState() != null ? action.getState().getName() : null)
                .stateId(action.getState() != null ? action.getState().getStateId() : null)
                .skillEffectsResults(
                        action.getSkillEffectsResults() == null
                                ? List.of()
                                : action.getSkillEffectsResults().stream()
                                .map(SkillEffectResultMapper::toDTO)
                                .toList()
                )
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    public static List<CombatActionDTO> toDTOList(List<CombatActionDomain> actions) {
        if (actions == null) return List.of();

        return actions.stream()
                .map(CombatActionMapper::toDTO)
                .toList();
    }

    //------------------------------------------------------------------------------------------------------------------

}