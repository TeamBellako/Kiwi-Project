package com.kiwi.features.combat.data.domain;

import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class CombatTurnResultDomain {

    private Long combatId;

    private int turnNumber;

    private List<CombatActionDomain> actions;

    private CombatGeneralStatus combatStatus;

    private boolean bonusActionPending;
}
