package com.kiwi.features.combat.data.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElementMultiplierDomain{

    private Long elementId;

    private String name;

    private int icon;

    private String description;

    private float multiplier;
}
