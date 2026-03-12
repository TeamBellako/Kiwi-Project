package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.data.enums.CombatActor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class ActorRuntime {

    private CombatActor actor;

    private int hp;
    private int maxHp;

    private int patk;
    private int matk;

    private int pdef;
    private int mdef;

    private int acc;
    private int eva;
    private int lck;

    private Map<Long, Float> elementMultipliers;
    private Map<Long, Float> statusResistances;

    private List<ActiveState> states;

    private Map<Long, SkillRuntime> skills;

    private Long lastSkillUsed;

    public void damage(int amount) {
        hp = Math.max(0, hp - amount);
    }

    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }
}