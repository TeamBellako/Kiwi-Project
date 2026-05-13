package com.kiwi.features.skills.data.persistence;

import com.kiwi.features.combat.data.persistence.EnemyPersistence;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "enemy_skill")
public class EnemySkillPersistence {

    @EmbeddedId
    private EnemySkillKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("enemyId")
    @JoinColumn(name = "enemy_id")
    private EnemyPersistence enemy;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    private SkillPersistence skill;
}