package com.kiwi.features.combat.data.enums;

import lombok.Getter;

@Getter
public enum CombatStateTypes {

    INVINCIBLE(1),
    POISON(2),
    FREEZE(3),
    CONFUSION(4),
    MUTIS(5),
    PRECISION(6),
    REGENERATION(7),
    FURY(8),
    SURVIVE(9),
    BOND(10),
    LOOP(11),
    STAT_DOWN(12),
    STAT_UP(13),
    BUFF_BLOCK(14),
    REVERSION(15);

    private final int id;

    CombatStateTypes(int id) {
        this.id = id;
    }

    public static CombatStateTypes fromId(int id) {
        for (CombatStateTypes s : values()) {
            if (s.id == id) return s;
        }
        throw new IllegalArgumentException("Invalid CombatState id: " + id);
    }
}
