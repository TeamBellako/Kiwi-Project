package com.kiwi.features.combat.data.dto;

import com.kiwi.features.skills.data.DTO.SkillEffectResultDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class CombatActionDTO {

    private String actor;     // USER / ENEMY

    private String actionType; // ActionType enum

    private String stateName;
    private Long stateId;
    private Float stateEffectValue;
    private List<Long> blockedSkills;

    private String skillName;
    private List<SkillEffectResultDTO> skillEffectsResults;
}