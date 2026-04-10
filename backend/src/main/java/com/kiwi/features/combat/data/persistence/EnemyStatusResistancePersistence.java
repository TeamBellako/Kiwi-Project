package com.kiwi.features.combat.data.persistence;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "enemy_status_resistances")
public class EnemyStatusResistancePersistence {

    @EmbeddedId
    private EnemyStatusResistanceKey id;

    @Column(name = "resistance", nullable = false)
    private Float resistance;
}