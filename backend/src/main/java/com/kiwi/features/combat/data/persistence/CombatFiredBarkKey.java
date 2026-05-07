package com.kiwi.features.combat.data.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class CombatFiredBarkKey implements Serializable {

    @Column(name = "combat_id")
    private Long combatId;

    @Column(name = "trigger_id")
    private Long triggerId;
}
