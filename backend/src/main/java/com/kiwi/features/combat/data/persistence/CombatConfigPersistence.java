package com.kiwi.features.combat.data.persistence;

import com.kiwi.features.sprites.data.BackgroundPersistence;
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

    @ManyToOne(optional = true)
    @JoinColumn(name = "background", nullable = true)
    private BackgroundPersistence background;

    @Column(name = "on_completed_action")
    private String onCompletedAction;

    @Column(name = "on_completed_entity")
    private String onCompletedEntity = "";

    @Column(name = "on_completed_entity_id")
    private int onCompletedEntityId = 0;
}