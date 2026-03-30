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

    @Column(name="user_hp")
    private Integer userHp;

    @Column(name="enemy_hp")
    private Integer enemyHp;

    @Column(name="turn_number")
    private int turnNumber;

    @Column(name = "ends_at")
    private Instant endsAt;

    @Enumerated(EnumType.STRING)
    @Column(name="combat_status")
    private CombatGeneralStatus combatStatus;
}