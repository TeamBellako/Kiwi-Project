package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.data.domain.CombatActionDomain;
import com.kiwi.features.combat.data.domain.CombatActiveStatusDomain;
import com.kiwi.features.combat.data.domain.CombatActorDomain;
import com.kiwi.features.combat.data.domain.CombatDomain;
import com.kiwi.features.combat.data.enums.CombatActionType;
import com.kiwi.features.combat.data.enums.CombatStateTypes;

import com.kiwi.features.combat.data.enums.StatType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class CombatStatusManager {

    private final Random random;

    // ----------------------------------------------------------------------------------------------------------------

    // TODO: habria que revisar prioridades porque no vas hacer confusion si estas freeze
    public void applyActiveStatesEffectsToActor(CombatActorDomain actor, CombatDomain combat)
    {
        for (CombatActiveStatusDomain state : actor.getActiveStatuses()) {

            CombatStateTypes stateType = CombatStateTypes.fromId(state.getStateId().intValue());

            switch (stateType) {
                case POISON:

                    int poisonDamage = Math.round(actor.getStats().getStat(StatType.MAX_HP) *  state.getValue());
                    actor.damage(poisonDamage, false);

                    CombatActionDomain poisonAction =
                            CombatActionDomain.builder()
                                    .actor(actor.getType())
                                    .actionType(CombatActionType.ACTOR_DAMAGED_BY_STATE)
                                    .state(state)
                                    .stateEffectValue((float) poisonDamage)
                                    .build();

                    combat.addAction(poisonAction);
                    break;
                case FREEZE:
                    if (random.nextInt(100) < 80) {
                        actor.setActionModifierType(CombatActionType.ACTOR_BLOCKED_BY_STATE);

                        CombatActionDomain freezeAction =
                                CombatActionDomain.builder()
                                        .actor(actor.getType())
                                        .actionType(CombatActionType.ACTOR_BLOCKED_BY_STATE)
                                        .state(state)
                                        .build();

                        combat.addAction(freezeAction);
                    }
                    break;
                case CONFUSION:
                case LOOP:
                    if(actor.getLastSkillUsed() == null){
                        break;
                    }

                    if (stateType == CombatStateTypes.CONFUSION && random.nextInt(100) > 40) {
                       break;
                    }

                    actor.setActionModifierType(CombatActionType.SKILL_REPEAT_BY_STATE);
                    CombatActionDomain action =
                            CombatActionDomain.builder()
                                    .actor(actor.getType())
                                    .actionType(CombatActionType.SKILL_REPEAT_BY_STATE)
                                    .state(state)
                                    .build();

                    combat.addAction(action);
                    break;
                case MUTIS:
                    List<Long> blockedSkillIds;

                    List<Long> skillIds =
                            new ArrayList<>(actor.getSkills().keySet());

                    Collections.shuffle(skillIds);

                    blockedSkillIds = skillIds.stream()
                            .limit(2)
                            .toList();

                    CombatActionDomain mutisAction =
                            CombatActionDomain.builder()
                                    .actor(actor.getType())
                                    .actionType(CombatActionType.BLOCKED_SKILLS_BY_STATE)
                                    .state(state)
                                    .blockedSkills(blockedSkillIds)
                                    .build();

                    combat.addAction(mutisAction);

                    actor.setBlockedSkills(blockedSkillIds);
                    break;

                case REGENERATION:
                    int heal = Math.round(actor.getStats().getStat(StatType.MAX_HP) * state.getValue());

                    actor.heal(heal);

                    CombatActionDomain regenAction =
                            CombatActionDomain.builder()
                                    .actor(actor.getType())
                                    .actionType(CombatActionType.ACTOR_HEALED_BY_STATE)
                                    .state(state)
                                    .stateEffectValue((float) heal)
                                    .build();

                    combat.addAction(regenAction);
                    break;
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

    public int getEffectiveStat(
            CombatActorDomain actor,
            StatType stat
    ) {
        int base = actor.getStats().getStat(stat);

        float multiplier = 1f;

        for (CombatActiveStatusDomain s : actor.getActiveStatuses()) {

            CombatStateTypes type =
                    CombatStateTypes.fromId(s.getStateId().intValue());

            if (type == CombatStateTypes.STAT_UP &&
                    s.getStatAffected() == stat) {

                multiplier *= s.getValue();
            }

            if (type == CombatStateTypes.STAT_DOWN &&
                    s.getStatAffected() == stat) {

                multiplier *= s.getValue();
            }

            if (type == CombatStateTypes.FURY &&
                    stat == StatType.PATK) {

                multiplier *= s.getValue();
            }
        }

        return Math.round(base * multiplier);
    }

    // ----------------------------------------------------------------------------------------------------------------

    public void reduceStatesTurnsToActor(CombatActorDomain actor, CombatDomain combat)
    {
        Iterator<CombatActiveStatusDomain> statesIt =
                actor.getActiveStatuses().iterator();

        while (statesIt.hasNext()) {
            CombatActiveStatusDomain state = statesIt.next();

            CombatActionDomain action =
                    CombatActionDomain.builder()
                            .actor(actor.getType())
                            .actionType(CombatActionType.STATUS_TURN_REDUCED)
                            .state(state)
                            .build();

            state.setRemainingTurns(
                    state.getRemainingTurns() - 1
            );

            if (state.getRemainingTurns() <= 0) {
                statesIt.remove();

                action.setActionType(CombatActionType.STATUS_FINISHED);
            }

            combat.addAction(action);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

}
