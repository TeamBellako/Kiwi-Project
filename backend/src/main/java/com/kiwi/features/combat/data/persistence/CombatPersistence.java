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

    @Column(name="time_max")
    private Integer timeMax;

    @Column(name="time_remaining")
    private Integer timeRemaining;

    @Column(name="user_hp")
    private Integer userHp;

    @Column(name="enemy_hp")
    private Integer enemyHp;

    @Column(name="turn_number")
    private int turnNumber;

    @Enumerated(EnumType.STRING)
    @Column(name="combat_status")
    private CombatGeneralStatus combatStatus;

    @Column(name="started_at")
    private Instant startedAt;
}