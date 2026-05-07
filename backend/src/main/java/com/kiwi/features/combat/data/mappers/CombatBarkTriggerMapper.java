package com.kiwi.features.combat.data.mappers;

import com.kiwi.features.combat.data.domain.CombatBarkTriggerDomain;
import com.kiwi.features.combat.data.dto.CombatBarkTriggerDTO;
import com.kiwi.features.combat.data.persistence.CombatBarkTriggerPersistence;

import java.util.List;

public class CombatBarkTriggerMapper {

    //------------------------------------------------------------------------------------------------------------------

    public static CombatBarkTriggerDomain toDomain(CombatBarkTriggerPersistence trigger) {
        return CombatBarkTriggerDomain.builder()
                .id(trigger.getId())
                .type(trigger.getType())
                .threshold(trigger.getThreshold())
                .skillId(trigger.getSkillId())
                .conversationId(trigger.getConversationId())
                .dismissMode(trigger.getDismissMode())
                .priority(trigger.getPriority())
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    public static CombatBarkTriggerDTO toDTO(CombatBarkTriggerDomain trigger) {
        return CombatBarkTriggerDTO.builder()
                .id(trigger.getId())
                .type(trigger.getType().name())
                .threshold(trigger.getThreshold())
                .skillId(trigger.getSkillId())
                .conversationId(trigger.getConversationId())
                .dismissMode(trigger.getDismissMode().name())
                .priority(trigger.getPriority())
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    public static List<CombatBarkTriggerDTO> toDTOList(List<CombatBarkTriggerPersistence> triggers) {
        return triggers.stream()
                .map(CombatBarkTriggerMapper::toDomain)
                .map(CombatBarkTriggerMapper::toDTO)
                .toList();
    }

    //------------------------------------------------------------------------------------------------------------------
}
