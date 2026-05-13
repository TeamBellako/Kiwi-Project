package com.kiwi.features.skills.data.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "user_skill_status")
public class UserSkillStatusPersistence {

    @EmbeddedId
    private UserSkillStatusKey id;

    @Column(name = "is_cooldown", nullable = false)
    private boolean isCooldown;

    @Column(name = "cooldown_until")
    private Instant cooldownUntil;

    @Column(name = "deck_slot", nullable = false)
    private int deckSlot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", insertable = false, updatable = false)
    private SkillPersistence skill;
}
