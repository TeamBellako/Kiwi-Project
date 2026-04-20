package com.kiwi.features.combat.data.persistence;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "enemies")
public class EnemyPersistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String sprite;

    private int maxHp;

    private int patk;
    private int matk;

    private int pdef;
    private int mdef;

    private int acc;
    private int eva;

    private int lck;
}