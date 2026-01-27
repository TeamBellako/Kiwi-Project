package com.kiwi.features.conversations.data;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
@Entity
@Table(name = "conversation_options")
public class ConversationOptionPersistence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ConversationPersistence conversation;

    @Column(name = "text", length = 500, nullable = false)
    private String text;

    @Column(name = "text_m", length = 500, nullable = false)
    private String textM;

    @Column(name = "text_w", length = 500, nullable = false)
    private String textW;

    @Column(name = "next_event_id", nullable = false)
    private Long nextEventId;

    @Column(name = "cost", nullable = true)
    private Integer cost;
}
