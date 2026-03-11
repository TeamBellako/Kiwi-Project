package com.kiwi.features.combat.engine;

import com.kiwi.features.combat.data.persistence.CombatPersistence;

public class EnemyAI {

    public Long chooseSkill(CombatPersistence context) {

        // versión simple
        return context.getEnemySkills().get(0);

    }
}