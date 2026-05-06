package com.kiwi.features.combat.data.persistence;

import com.kiwi.features.combat.data.enums.BarkDismissMode;
import com.kiwi.features.combat.data.enums.BarkTriggerType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "combat_bark_triggers")
public class CombatBarkTriggerPersistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "combat_config_id", nullable = false)
    private Long combatConfigId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private BarkTriggerType type;

    @Column(name = "threshold")
    private Float threshold;

    @Column(name = "skill_id")
    private Long skillId;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "dismiss_mode", nullable = false)
    private BarkDismissMode dismissMode;

    @Column(name = "priority", nullable = false)
    private int priority;
}
