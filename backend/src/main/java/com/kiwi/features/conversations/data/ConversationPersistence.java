package com.kiwi.features.conversations.data;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
@Entity
@Table(name = "conversations")
public class ConversationPersistence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ConversationType type;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "sprite", nullable = false)
    private String sprite;

    @Column(name = "background")
    private String background;

    @Column(name = "fx")
    private String fx;

    @Column(name = "dark", nullable = false)
    private Boolean dark;

    @Column(name = "delay_start_ms")
    private Integer delayStartMs;

    @Column(name = "delay_end_ms")
    private Integer delayEndMs;
    
    @Column(name = "dialog", length = 1000, nullable = false)
    private String dialog;

    @Column(name = "dialog_m", length = 1000, nullable = false)
    private String dialogM;

    @Column(name = "dialog_w", length = 1000, nullable = false)
    private String dialogW;

    @Enumerated(EnumType.STRING)
    @Column(name = "next_event", nullable = false)
    private NextEventType nextEvent;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "incidence_for_next_event")
    private String incidenceForNextEvent;

    @Column(name = "fallback_event_id")
    private Long fallbackEventId;

    @Column(name = "incidence_name_to_set")
    private String incidenceNameToSet;

    @Column(name = "incidence_value_to_set")
    private Boolean incidenceValueToSet = true;

    @Column(name = "on_completed_action")
    private String onCompletedAction;

    @Column(name = "on_completed_entity")
    private String onCompletedEntity = "";

    @Column(name = "on_completed_entity_id")
    private int onCompletedEntityId = 0;
}
