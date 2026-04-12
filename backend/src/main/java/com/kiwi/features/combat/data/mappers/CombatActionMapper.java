package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.dto.CombatActiveStatusDTO;
import com.kiwi.features.combat.data.enums.CombatActionType;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.persistence.CombatLogPersistence;
import com.kiwi.features.skills.data.DTO.SkillEffectResultDTO;
import com.kiwi.features.skills.data.enums.SkillEffectResultType;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CombatActionMapper {

    //------------------------------------------------------------------------------------------------------------------

    public static CombatActionDTO toDTO(CombatLogPersistence log) {

        CombatActionDTO.CombatActionDTOBuilder builder = CombatActionDTO.builder()
                .actor(log.getActor() != null ? log.getActor().name() : null)
                .actionType(log.getCombatActionType() != null ? log.getCombatActionType().name() : null)
                .skillName(log.getSkillName())
                .stateId(log.getStateId())
                .stateName(log.getStateName())
                .value(log.getValue())
                .blockedSkills(blockedSkillsToList(log.getBlockedSkills()));

        if (log.getEffectType() != null) {

            SkillEffectResultDTO.SkillEffectResultDTOBuilder effectBuilder =
                    SkillEffectResultDTO.builder()
                            .typeResult(log.getEffectType().name())
                            .target(log.getTarget() != null ? log.getTarget().name() : null)
                            .value(log.getValue())
                            .critic(Boolean.TRUE.equals(log.getCritic()));

            if (log.getStateId() != null) {
                effectBuilder.appliedStatus(
                        CombatActiveStatusDTO.builder()
                                .stateId(log.getStateId())
                                .name(log.getStateName())
                                .remainingTurns(log.getStatusDuration())
                                .value(log.getValue())
                                .build()
                );
            }

            builder.effects(List.of(effectBuilder.build()));
        }

        return builder.build();
    }

    //------------------------------------------------------------------------------------------------------------------

    public static List<Long> blockedSkillsToList(String blockedSkills) {

        if (blockedSkills == null || blockedSkills.isBlank()) {
            return Collections.emptyList();
        }

        return Arrays.stream(blockedSkills.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
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

    public static List<CombatLogPersistence> mapCombatAction(
            CombatActionDTO action,
            Long combatId,
            int turnNumber
    ) {
        CombatActionType type = CombatActionType.valueOf(action.getActionType());

        return switch (type) {

            case SKILL_USED -> mapSkillUsed(action, combatId, turnNumber);

            case ACTOR_BLOCKED_BY_STATE,
                 SKILL_REPEAT_BY_STATE,
                 STATUS_TURN_REDUCED,
                 STATUS_FINISHED -> List.of(
                    base(action, combatId, turnNumber)
                            .stateName(action.getStateName())
                            .stateId(action.getStateId())
                            .build()
            );

            case ACTOR_DAMAGED_BY_STATE -> List.of(
                    base(action, combatId, turnNumber)
                            .stateName(action.getStateName())
                            .stateId(action.getStateId())
                            .value(action.getValue())
                            .build()
            );

            case BLOCKED_SKILLS_BY_STATE,
                 RELEASED_SKILLS_BY_STATE -> List.of(
                    base(action, combatId, turnNumber)
                            .stateName(action.getStateName())
                            .stateId(action.getStateId())
                            .blockedSkills(
                                    CombatActionMapper.blockedSkillsToString(action.getBlockedSkills())
                            )
                            .build()
            );

            case SKIP, TIMEOUT -> List.of(
                    base(action, combatId, turnNumber).build()
            );
        };
    }

    //------------------------------------------------------------------------------------------------------------------

    private static List<CombatLogPersistence> mapSkillUsed(
            CombatActionDTO action,
            Long combatId,
            int turnNumber
    ) {

        if (action.getEffects() == null || action.getEffects().isEmpty()) {
            return List.of(
                    base(action, combatId, turnNumber)
                            .skillName(action.getSkillName())
                            .build()
            );
        }

        return action.getEffects().stream()
                .map(effect -> {
                    CombatLogPersistence.CombatLogPersistenceBuilder builder =
                            base(action, combatId, turnNumber)
                                    .skillName(action.getSkillName())
                                    .effectType(SkillEffectResultType.valueOf(effect.getTypeResult()))
                                    .target(CombatActorType.valueOf(effect.getTarget()))
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

    private static CombatLogPersistence.CombatLogPersistenceBuilder base(
            CombatActionDTO action,
            Long combatId,
            int turnNumber
    ) {
        return CombatLogPersistence.builder()
                .combatId(combatId)
                .turnNumber(turnNumber)
                .actor(CombatActorType.valueOf(action.getActor()))
                .combatActionType(CombatActionType.valueOf(action.getActionType()))
                .createdAt(Instant.now());
    }

    //------------------------------------------------------------------------------------------------------------------

}