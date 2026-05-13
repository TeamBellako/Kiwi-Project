package com.kiwi.features.combat.data.persistence;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "combat_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatConfigPersistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="enemy_id", nullable=false)
    private Long enemyId;

    @Column(name="time_limit")
    private Integer timeLimit;

    @Column(name = "background")
    private String background;

    @Column(name = "sfx")
    private String sfx;

    @Column(name = "on_completed_action")
    private String onCompletedAction;

    @Column(name = "on_completed_entity")
    private String onCompletedEntity = "";

    @Column(name = "on_completed_entity_id")
    private int onCompletedEntityId = 0;
}