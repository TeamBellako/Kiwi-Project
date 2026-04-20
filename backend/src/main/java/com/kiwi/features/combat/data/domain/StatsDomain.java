package com.kiwi.features.combat.data.domain;

import lombok.*;

@Getter
@Setter
@Builder
public class StatsDomain {

    private int currentHp;
    private int maxHp;

    private int patk;
    private int matk;

    private int pdef;
    private int mdef;

    private int acc;
    private int eva;

    private int lck;
}
