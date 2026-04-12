package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.data.domain.CombatActorDomain;
import com.kiwi.features.combat.data.domain.CombatDomain;
import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.skills.data.enums.SkillEffectTargetType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CombatContext {

    private final CombatDomain combat;

    private final CombatActorDomain user;
    private final CombatActorDomain enemy;

    private final List<CombatActionDTO> actionsDTOs = new ArrayList<>();

    //------------------------------------------------------------------------------------------------------------------

    public CombatContext(
            CombatDomain combat,
            CombatActorDomain user,
            CombatActorDomain enemy
    ) {
        this.combat = combat;
        this.user = user;
        this.enemy = enemy;
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

    public void addAction(CombatActionDTO action) {
        actionsDTOs.add(action);
    }

    //------------------------------------------------------------------------------------------------------------------
}