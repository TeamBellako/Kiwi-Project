package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.data.domain.CombatStatusEffectDomain;
import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.dto.CombatStateAppliedDTO;
import com.kiwi.features.combat.data.enums.CombatActor;
import com.kiwi.features.combat.data.persistence.CombatPersistence;

import java.util.List;

public class CombatStateService {

    public boolean canAct(ActorRuntime actor) {
        return false;
    }

    public List<CombatStateAppliedDTO> getActiveStates(CombatPersistence combat, CombatActor combatActor) {
        return null;
    }

    public CombatActionDTO buildSkipAction(CombatActor combatActor) {
    }

    public void applyCurrentStates(CombatContext context) {
    }

    public void applyBurn(ActorRuntime actor) {

        int damage =
                (int)(actor.getMaxHp() * 0.05);

        actor.damage(damage);
    }

    public void updateStates(CombatContext context) {

    }
}