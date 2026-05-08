package com.kiwi.features.combat.data.persistence;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_stats")
public class UserStatsPersistence {

    @Id
    @Column(name="user_id")
    private Long userId;

    private int maxHp;

    private int shield;

    private int patk;
    private int matk;

    private int pdef;
    private int mdef;

    private int acc;
    private int eva;

    private int lck;
}