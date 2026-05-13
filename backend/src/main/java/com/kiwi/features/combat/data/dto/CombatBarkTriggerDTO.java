package com.kiwi.features.combat.data.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatBarkTriggerDTO {

    private Long id;

    private String type;

    private Float threshold;

    private Long skillId;

    private Long conversationId;

    private String dismissMode;

    private int priority;
}
