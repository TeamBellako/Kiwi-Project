package com.kiwi.features.combat.data.element;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CombatElementDomain {

    private Long id;
    private String name;
    private int icon;
    private String description;
}