package com.kiwi.features.combat.data.persistence;

import com.kiwi.features.combat.data.enums.CombatActorType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class CombatLastSkillKey implements Serializable {

    @Column(name = "combat_id")
    private Long combatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor")
    private CombatActorType actor;
}