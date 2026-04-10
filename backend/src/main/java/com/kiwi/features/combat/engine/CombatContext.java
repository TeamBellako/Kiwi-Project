package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.data.domain.ActorDomain;
import com.kiwi.features.combat.data.domain.CombatDomain;
import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.skills.data.enums.SkillEffectTargetType;
import com.kiwi.features.combat.data.persistence.CombatPersistence;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public class CombatContext {

    private final Random random = new Random();

    private final CombatDomain combat;

    private final ActorDomain user;
    private final ActorDomain enemy;

    private final List<CombatActionDTO> actions = new ArrayList<>();

    public CombatContext(
            CombatDomain combat,
            ActorDomain user,
            ActorDomain enemy
    ) {
        this.combat = combat;
        this.user = user;
        this.enemy = enemy;
    }

    public ActorDomain getActor(CombatActorType actor) {
        return actor == CombatActorType.USER ? user : enemy;
    }

    public ActorDomain getTarget(CombatActorType actor, SkillEffectTargetType targetType) {

        if (actor == CombatActorType.USER){
            if (targetType == SkillEffectTargetType.SELF){
                return user;
            } // Ally check goes here in the future
            else{
               return enemy;
            }
        }
        else{
            if (targetType == SkillEffectTargetType.SELF){
                return enemy;
            } else {
                return user;
            }
        }
    }

    public void addAction(CombatActionDTO action) {
        actions.add(action);
    }

}