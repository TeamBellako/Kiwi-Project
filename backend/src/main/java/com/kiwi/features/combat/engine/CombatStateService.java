package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.data.dto.CombatStateAppliedDTO;
import com.kiwi.features.combat.data.enums.CombatActor;
import com.kiwi.features.combat.data.persistence.CombatPersistence;

import java.util.List;

public class CombatStateService {

    public void processTurnStates(CombatPersistence context) {

        context.getStates().forEach(state -> {

            switch (state.getStateId()) {

                case 1 -> applyBurn(context, state);
                case 2 -> applyPoison(context, state);

            }

        });
    }

    private void applyBurn(CombatContext context, CombatStatusEffectDomain state) {

        int damage = (int)(context.getEnemyMaxHp() * state.getValue());

        context.damageEnemy(damage);

    }

    public List<CombatStateAppliedDTO> getActiveStates(CombatPersistence combat, CombatActor combatActor) {
        return null;
    }
}