package com.kiwi.features.combat.data.enums;

public enum AttackType {
    PHYSICAL,
    MAGICAL;

    public boolean isPhysical() {
        return this == PHYSICAL;
    }

    public boolean isMagical() {
        return this == MAGICAL;
    }
}