package com.kiwi.features.combat.data.domain;

import com.kiwi.features.combat.data.enums.BarkDismissMode;
import com.kiwi.features.combat.data.enums.BarkTriggerType;
import lombok.*;

@Getter
@Setter
@Builder
public class CombatBarkTriggerDomain {

    private Long id;

    private BarkTriggerType type;

    private Float threshold;

    private Long skillId;

    private Long conversationId;

    private BarkDismissMode dismissMode;

    private int priority;
}
