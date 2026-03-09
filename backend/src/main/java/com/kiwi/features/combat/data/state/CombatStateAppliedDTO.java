package com.kiwi.features.combat.data.state;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatStateAppliedDTO {

    private Long stateId;

    private String name;

    private int icon;

    private String description;

    private int remainingTurns;

    private Float value;
}