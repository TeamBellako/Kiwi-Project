package com.kiwi.features.combat.data.persistence;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "enemy_elemental_multipliers")
public class EnemyElementMultiplierPersistence {

    @EmbeddedId
    private EnemyElementalMultiplierKey id;

    @Column(name = "multiplier", nullable = false)
    private Float multiplier;
}