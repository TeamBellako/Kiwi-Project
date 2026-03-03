package com.kiwi.features.conversations.data;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
@Entity
@Table(name = "user_conversation_selection")
@IdClass(UserConversationSelectionId.class)
public class UserConversationSelectionPersistence {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "option_id", nullable = false)
    private Long optionId;
}
