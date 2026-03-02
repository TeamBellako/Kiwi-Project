package com.kiwi.features.conversations.data;

import lombok.*;

import java.io.Serializable;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class UserConversationSelectionId implements Serializable {
    private Long userId;
    private Long conversationId;
}
