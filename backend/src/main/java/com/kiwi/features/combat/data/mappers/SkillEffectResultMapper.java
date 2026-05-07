package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.skills.data.domain.SkillEffectResultDomain;
import com.kiwi.features.combat.data.dto.CombatActiveStatusDTO;
import com.kiwi.features.skills.data.DTO.SkillEffectResultDTO;

public class SkillEffectResultMapper {

    public static SkillEffectResultDTO toDTO(SkillEffectResultDomain e) {

        return SkillEffectResultDTO.builder()
                .typeResult(e.getTypeResult() != null ? e.getTypeResult().name() : null)
                .target(e.getTarget() != null ? e.getTarget().name() : null)
                .statAffected(e.getStatAffected() != null ? e.getStatAffected().name() : null)
                .value(e.getValue())
                .critic(e.isCritic())
                .appliedStatus(
                        e.getAppliedStatus() == null
                                ? null
                                : CombatActiveStatusDTO.builder()
                                .stateId(e.getAppliedStatus().getStateId())
                                .name(e.getAppliedStatus().getName())
                                .remainingTurns(e.getAppliedStatus().getRemainingTurns())
                                .value(e.getAppliedStatus().getValue())
                                .build()
                )
                .turns(e.getTurns())
                .resetCooldownSkills(e.getResetCooldownSkills())
                .build();
    }
}
