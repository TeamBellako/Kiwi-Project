package com.kiwi.features.combat.data.domain;

import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.skills.data.enums.SkillEffectTargetType;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CombatDomain {

    private final Long id;

    private final Long combatConfigId;

    private final CombatActorDomain user;
    private final CombatActorDomain enemy;

    private final List<CombatActionDomain> actions = new ArrayList<>();

    private int turnNumber;

    private CombatGeneralStatus combatStatus;

    private Instant endsAt;

    private List<CombatBarkTriggerDomain> barks = List.of();

    private List<Long> firedBarkIds = List.of();

    //------------------------------------------------------------------------------------------------------------------

    public CombatDomain(
            Long id, Long combatConfigId, CombatActorDomain user,
            CombatActorDomain enemy, int turnNumber, CombatGeneralStatus combatStatus, Instant endsAt
    ) {
        this.id = id;
        this.combatConfigId = combatConfigId;
        this.user = user;
        this.enemy = enemy;
        this.turnNumber = turnNumber;
        this.combatStatus = combatStatus;
        this.endsAt = endsAt;
    }

    //------------------------------------------------------------------------------------------------------------------

    public CombatActorDomain getActor(CombatActorType actor) {

        return actor == CombatActorType.USER ? user : enemy;
    }

    //------------------------------------------------------------------------------------------------------------------

    public CombatActorDomain getTarget(CombatActorType actor, SkillEffectTargetType targetType) {

        if (actor == CombatActorType.USER){
            if (targetType == SkillEffectTargetType.SELF)
            {
                return user;
            } // Ally check goes here in the future
            else{
                return enemy;
            }
        }
        else{
            if (targetType == SkillEffectTargetType.SELF)
            {
                return enemy;
            } else {
                return user;
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public void addAction(CombatActionDomain action) {
        actions.add(action);
    }

    //------------------------------------------------------------------------------------------------------------------
}