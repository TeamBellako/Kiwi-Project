package com.kiwi.features.conversations.data;

import com.kiwi.features.sprites.data.ExpressionPersistence;
import com.kiwi.features.sprites.data.SpritePersistence;
import com.kiwi.features.sprites.data.BackgroundPersistence;
import com.kiwi.features.sprites.data.FxPersistence;

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

    @ManyToOne(optional = false)
    @JoinColumn(name = "sprite", nullable = false)
    private SpritePersistence sprite;

    @ManyToOne(optional = false)
    @JoinColumn(name = "expresion", nullable = false)
    private ExpressionPersistence expresion;

    @ManyToOne(optional = true)
    @JoinColumn(name = "background", nullable = true)
    private BackgroundPersistence background;

    @ManyToOne(optional = true)
    @JoinColumn(name = "fx", nullable = true)
    private FxPersistence fx;

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
}
