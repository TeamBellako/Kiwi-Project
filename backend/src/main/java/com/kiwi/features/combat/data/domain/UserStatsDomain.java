package com.kiwi.features.combat.data.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDomain {

    private Long userId;

    private int maxHp;

    private int patk;
    private int matk;

    private int pdef;
    private int mdef;

    private int acc;
    private int eva;

    private int lck;
}