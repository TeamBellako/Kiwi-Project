package com.kiwi.features.combat.data.persistence;

import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "combats")
public class CombatPersistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="combat_config_id", nullable=false)
    private Long combatConfigId;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="enemy_id", nullable = false)
    private Long enemyId;

    // USER STATS SNAPSHOT
    @Column(name="user_hp", nullable = false)
    private Integer userHp;

    @Column(name="user_max_hp", nullable = false)
    private Integer userMaxHp;

    @Column(name="user_shield", nullable = false)
    private Integer userShield;

    @Column(name="user_patk", nullable = false)
    private Integer userPatk;

    @Column(name="user_matk", nullable = false)
    private Integer userMatk;

    @Column(name="user_pdef", nullable = false)
    private Integer userPdef;

    @Column(name="user_mdef", nullable = false)
    private Integer userMdef;

    @Column(name="user_acc", nullable = false)
    private Integer userAcc;

    @Column(name="user_eva", nullable = false)
    private Integer userEva;

    @Column(name="user_lck", nullable = false)
    private Integer userLck;

    // ENEMY STATS SNAPSHOT
    @Column(name="enemy_hp", nullable = false)
    private Integer enemyHp;

    @Column(name="enemy_max_hp", nullable = false)
    private Integer enemyMaxHp;

    @Column(name="enemy_shield", nullable = false)
    private Integer enemyShield;

    @Column(name="enemy_patk", nullable = false)
    private Integer enemyPatk;

    @Column(name="enemy_matk", nullable = false)
    private Integer enemyMatk;

    @Column(name="enemy_pdef", nullable = false)
    private Integer enemyPdef;

    @Column(name="enemy_mdef", nullable = false)
    private Integer enemyMdef;

    @Column(name="enemy_acc", nullable = false)
    private Integer enemyAcc;

    @Column(name="enemy_eva", nullable = false)
    private Integer enemyEva;

    @Column(name="enemy_lck", nullable = false)
    private Integer enemyLck;

    @Column(name="turn_number", nullable = false)
    private int turnNumber;

    @Column(name = "ends_at")
    private Instant endsAt;

    @Enumerated(EnumType.STRING)
    @Column(name="combat_status", nullable = false)
    private CombatGeneralStatus combatStatus;
}