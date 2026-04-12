package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.data.domain.CombatActiveStatusDomain;
import com.kiwi.features.combat.data.domain.CombatActorDomain;
import com.kiwi.features.combat.data.dto.CombatActionDTO;
import com.kiwi.features.combat.data.enums.CombatActionType;
import com.kiwi.features.combat.data.enums.CombatStateTypes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class CombatStatusManager {

    private final Random random;

    // ----------------------------------------------------------------------------------------------------------------

    public void applyActiveStatesEffectsToActor(CombatActorDomain actor, CombatContext context)
    {
        for (CombatActiveStatusDomain state : actor.getStates()) {

            CombatStateTypes stateType = CombatStateTypes.fromId(state.getStateId().intValue());

            switch (stateType) {
                case BURN:
                    int burnDamage = applyDamage(actor, state.getValue());
                    CombatActionDTO burnAction =
                            CombatActionDTO.builder()
                                    .actor(actor.getType().name())
                                    .actionType(CombatActionType.ACTOR_DAMAGED_BY_STATE.name())
                                    .stateName(state.getName())
                                    .value((float) burnDamage)
                                    .build();

                    context.addAction(burnAction);
                    break;

                case POISON:
                    int posionDamage = applyDamage(actor, state.getValue());
                    CombatActionDTO posionAction =
                            CombatActionDTO.builder()
                                    .actor(actor.getType().name())
                                    .actionType(CombatActionType.ACTOR_DAMAGED_BY_STATE.name())
                                    .stateName(state.getName())
                                    .value((float) posionDamage)
                                    .build();

                    context.addAction(posionAction);
                    break;
                case FREEZE:
                    if (random.nextInt(100) < 80) {
                        actor.setActionModifierType(CombatActionType.ACTOR_BLOCKED_BY_STATE);

                        CombatActionDTO freezeAction =
                                CombatActionDTO.builder()
                                        .actor(actor.getType().name())
                                        .actionType(CombatActionType.ACTOR_BLOCKED_BY_STATE.name())
                                        .stateName(state.getName())
                                        .build();

                        context.addAction(freezeAction);
                    }
                    break;
                case CONFUSION:
                    if (actor.getLastSkillUsed() != null && random.nextInt(100) < 40) {
                        actor.setActionModifierType(CombatActionType.SKILL_REPEAT_BY_STATE);

                        CombatActionDTO action =
                                CombatActionDTO.builder()
                                        .actor(actor.getType().name())
                                        .actionType(CombatActionType.SKILL_REPEAT_BY_STATE.name())
                                        .stateName(state.getName())
                                        .build();

                        context.addAction(action);
                    }
                    break;
                case MUTIS:
                    List<Long> blockedSkillIds;

                    List<Long> skillIds =
                            new ArrayList<>(actor.getSkills().keySet());

                    Collections.shuffle(skillIds);

                    blockedSkillIds = skillIds.stream()
                            .limit(2)
                            .toList();

                    CombatActionDTO action =
                            CombatActionDTO.builder()
                                    .actor(actor.getType().name())
                                    .actionType(CombatActionType.BLOCKED_SKILLS_BY_STATE.name())
                                    .stateName(state.getName())
                                    .blockedSkills(blockedSkillIds)
                                    .build();

                    context.addAction(action);

                    blockedSkillIds.forEach(actor.getSkills()::remove);
                    break;
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

    private int applyDamage(CombatActorDomain actor, Float value)
    {
        int damage = Math.round(actor.getMaxHp() * value);
        actor.damage(damage);
        return damage;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public float calculateStateMultiplier(CombatActorDomain attacker, CombatActorDomain victim)
    {
        float multiplier = 1f;

        for (CombatActiveStatusDomain s : attacker.getStates()) {

            if (s.getStateId() == 10) { // ATK UP
                multiplier *= 1.5f;
            }

            if (s.getStateId() == 11) { // ATK DOWN
                multiplier *= 0.5f;
            }
        }

        for (CombatActiveStatusDomain s : victim.getStates()) {

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

    public void reduceStatesTurnsToActor(CombatActorDomain actor, CombatContext context)
    {
        Iterator<CombatActiveStatusDomain> statesIt =
                actor.getStates().iterator();

        while (statesIt.hasNext()) {
            CombatActiveStatusDomain state = statesIt.next();

            CombatActionDTO action =
                    CombatActionDTO.builder()
                            .actor(actor.getType().name())
                            .actionType(CombatActionType.STATUS_TURN_REDUCED.name())
                            .stateName(state.getName())
                            .build();

            state.setRemainingTurns(
                    state.getRemainingTurns() - 1
            );

            if (state.getRemainingTurns() <= 0) {
                statesIt.remove();

                action.setActionType(CombatActionType.STATUS_FINISHED.name());
            }

            context.addAction(action);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

}
