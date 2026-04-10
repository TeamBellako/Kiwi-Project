package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.dto.CombatActiveStatusDTO;
import com.kiwi.features.combat.data.persistence.CombatLogPersistence;
import com.kiwi.features.skills.data.DTO.SkillEffectResultDTO;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CombatActionMapper {

    public static CombatActionDTO toDTO(CombatLogPersistence log) {

        CombatActionDTO.CombatActionDTOBuilder builder = CombatActionDTO.builder()
                .actor(log.getActor() != null ? log.getActor().name() : null)
                .actionType(log.getActionType() != null ? log.getActionType().name() : null)
                .value(log.getValue() != null ? Math.round(log.getValue()) : 0)
                .skillName(log.getSkillName())
                .blockedSkills(parseBlockedSkills(log.getBlockedSkills()));

        if (log.getEffectType() != null) {

            SkillEffectResultDTO.SkillEffectResultDTOBuilder effectBuilder =
                    SkillEffectResultDTO.builder()
                            .typeResult(log.getEffectType().name())
                            .target(log.getTarget() != null ? log.getTarget().name() : null)
                            .value(log.getValue())
                            .critic(Boolean.TRUE.equals(log.getCritic()));

            if (log.getStateId() != null) {
                effectBuilder.activeStatus(
                        CombatActiveStatusDTO.builder()
                                .stateId(log.getStateId())
                                .remainingTurns(
                                        log.getStatusDuration() != null
                                                ? log.getStatusDuration()
                                                : 0
                                )
                                .build()
                );
            }

            builder.effects(List.of(effectBuilder.build()));
        } else {
            builder.effects(Collections.emptyList());
        }

        return builder.build();
    }

    // -----------------------------------------------------------------------------------------------------------------

    private static List<Long> parseBlockedSkills(String blockedSkills) {

        if (blockedSkills == null || blockedSkills.isBlank()) {
            return Collections.emptyList();
        }

        return Arrays.stream(blockedSkills.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}