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
public class EnemyElementalMultiplierKey implements Serializable {

    @Column(name = "enemy_id")
    private Long enemyId;

    @Column(name = "element_id")
    private Long elementId;
}