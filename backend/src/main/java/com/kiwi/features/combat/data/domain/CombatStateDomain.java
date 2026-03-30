package com.kiwi.features.combat.data.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CombatStateDomain {

    private Long id;

    private String name;

    private int icon;

    private String description;
}