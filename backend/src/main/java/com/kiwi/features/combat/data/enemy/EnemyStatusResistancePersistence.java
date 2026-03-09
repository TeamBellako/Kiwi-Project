package com.kiwi.features.combat.data.enemy;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long enemyId;
    private Long stateId;
    private float resistance;
}