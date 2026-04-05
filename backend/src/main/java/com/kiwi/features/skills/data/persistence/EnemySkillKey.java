package com.kiwi.features.skills.data.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Embeddable
public class EnemySkillKey implements Serializable {

    @Column(name = "enemy_id")
    private Long enemyId;

    @Column(name = "skill_id")
    private Long skillId;
}