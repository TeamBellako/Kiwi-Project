package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.data.dto.CombatStateAppliedDTO;
import com.kiwi.features.combat.data.enums.CombatActorType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Component
public class CombatStateService {

    private final Random random = new Random();

    public void onTurnStart(CombatContext context)
    {
        applyActiveStates(context.getUser());
        applyActiveStates(context.getEnemy());
    }

    // ----------------------------------------------------------------------------------------------------------------

    private void applyActiveStates(ActorRuntime actor)
    {
        for (ActiveState state : actor.getStates()) {

            if (state.getStateId() == 1) { // BURN
                applyDamage(actor);
            }

            if (state.getStateId() == 2) { // POISON
                applyDamage(actor);
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

    private void applyDamage(ActorRuntime actor)
    {
        int damage = (int) (actor.getMaxHp() * 0.05);
        actor.damage(damage);
    }

    // ----------------------------------------------------------------------------------------------------------------

    public boolean canAct(CombatContext context, CombatActorType actor)
    {
        ActorRuntime runtime = context.getActor(actor);

        for (ActiveState state : runtime.getStates()) {

            if (state.getStateId() == 3) { // FREEZE

                int chance = random.nextInt(100);

                if (chance < 80) {
                    return false;
                }
            }
        }

        return true;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public float calculateStateMultiplier(ActorRuntime attacker, ActorRuntime victim)
    {
        float multiplier = 1f;

        for (ActiveState s : attacker.getStates()) {

            if (s.getStateId() == 10) { // ATK UP
                multiplier *= 1.5f;
            }

            if (s.getStateId() == 11) { // ATK DOWN
                multiplier *= 0.5f;
            }
        }

        for (ActiveState s : victim.getStates()) {

            if (s.getStateId() == 12) { // DEF UP
                multiplier *= 0.5f;
            }

            if (s.getStateId() == 13) { // DEF DOWN
                multiplier *= 1.5f;
            }
        }

        return multiplier;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public void onTurnEnd(CombatContext context)
    {
        reduceStates(context.getUser());
        reduceStates(context.getEnemy());
    }

    // ----------------------------------------------------------------------------------------------------------------

    private void reduceStates(ActorRuntime actor)
    {
        Iterator<ActiveState> it =
                actor.getStates().iterator();

        while (it.hasNext()) {

            ActiveState state = it.next();

            state.setRemainingTurns(
                    state.getRemainingTurns() - 1
            );

            if (state.getRemainingTurns() <= 0) {
                it.remove();
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

    public CombatStateAppliedDTO buildStateDTO(ActiveState state)
    {
        return CombatStateAppliedDTO.builder()
                .stateId(state.getStateId())
                .remainingTurns(state.getRemainingTurns())
                .value(state.getValue())
                .build();
    }

    // ----------------------------------------------------------------------------------------------------------------

    public List<CombatStateAppliedDTO> getActiveStates(CombatContext context, CombatActorType combatActorType)
    {
        List<CombatStateAppliedDTO> activeStates = new ArrayList<>();

        ActorRuntime runtime = context.getActor(combatActorType);

        for (ActiveState state : runtime.getStates()) {
            activeStates.add(buildStateDTO(state));
        }

        return activeStates;
    }

    // ----------------------------------------------------------------------------------------------------------------

}