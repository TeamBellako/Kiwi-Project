package com.kiwi.features.combat.data.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class EnemyStatusResistanceKey implements Serializable {

    @Column(name = "enemy_id")
    private Long enemyId;

    @Column(name = "state_id")
    private Long stateId;
}