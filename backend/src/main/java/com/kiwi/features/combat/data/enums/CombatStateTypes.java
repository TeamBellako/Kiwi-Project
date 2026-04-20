package com.kiwi.features.combat.data.enums;

public enum CombatStateTypes {

    BURN(1),
    POISON(2),
    FREEZE(3),
    CONFUSION(4),
    MUTIS(5);

    private final int id;

    CombatStateTypes(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static CombatStateTypes fromId(int id) {
        for (CombatStateTypes s : values()) {
            if (s.id == id) return s;
        }
        throw new IllegalArgumentException("Invalid id: " + id);
    }
}
