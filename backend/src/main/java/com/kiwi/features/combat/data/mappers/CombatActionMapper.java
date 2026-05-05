package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.domain.CombatActionDomain;
import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.dto.CombatActiveStatusDTO;
import com.kiwi.features.combat.data.persistence.CombatLogPersistence;
import com.kiwi.features.skills.data.DTO.SkillEffectResultDTO;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

            case ACTOR_HEALED_BY_STATE,
                 ACTOR_DAMAGED_BY_STATE -> List.of(
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

            case SKIP, TIMEOUT, ABANDON -> List.of(
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
                                    .statAffected(effect.getStatAffected())
                                    .value(effect.getValue())
                                    .critic(effect.isCritic());

                    if (effect.getAppliedStatus() != null) {
                        builder
                                .stateId(effect.getAppliedStatus().getStateId())
                                .statAffected(effect.getStatAffected())
                                .value(effect.getValue())
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

    public static CombatActionDTO toDTOGroup(List<CombatLogPersistence> logs) {

        CombatLogPersistence first = logs.get(0);

        return CombatActionDTO.builder()
                .actor(first.getActor() != null ? first.getActor().name() : null)
                .actionType(first.getCombatActionType() != null ? first.getCombatActionType().name() : null)

                .skillName(first.getSkillName())

                .stateName(first.getStateName())
                .stateId(first.getStateId())
                .stateEffectValue(first.getValue())

                .blockedSkills(stringToBlockedSkills(first.getBlockedSkills()))

                .skillEffectsResults(
                        logs.stream()
                                .map(CombatActionMapper::toEffectDTO)
                                .filter(Objects::nonNull)
                                .toList()
                )

                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    private static SkillEffectResultDTO toEffectDTO(CombatLogPersistence log) {

        if (log.getEffectType() == null) {
            return null;
        }

        SkillEffectResultDTO.SkillEffectResultDTOBuilder builder =
                SkillEffectResultDTO.builder()
                        .typeResult(log.getEffectType().name())
                        .target(log.getTarget() != null ? log.getTarget().name() : null)
                        .statAffected(log.getStatAffected() != null ? log.getStatAffected().name() : null)
                        .value(log.getValue())
                        .critic(Boolean.TRUE.equals(log.getCritic()));

        if (log.getStateId() != null) {
            builder.appliedStatus(
                    CombatActiveStatusDTO.builder()
                            .stateId(log.getStateId())
                            .name(log.getStateName())
                            .remainingTurns(log.getStatusDuration())
                            .value(log.getValue())
                            .build()
            );
        }

        return builder.build();
    }

    //------------------------------------------------------------------------------------------------------------------

    private static List<Long> stringToBlockedSkills(String value) {
        if (value == null || value.isBlank()) return List.of();

        return Arrays.stream(value.split(","))
                .map(Long::valueOf)
                .toList();
    }

    //------------------------------------------------------------------------------------------------------------------

}