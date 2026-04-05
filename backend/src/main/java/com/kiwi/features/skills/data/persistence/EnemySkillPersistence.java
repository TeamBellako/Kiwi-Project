package com.kiwi.features.skills.data.persistence;

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
}