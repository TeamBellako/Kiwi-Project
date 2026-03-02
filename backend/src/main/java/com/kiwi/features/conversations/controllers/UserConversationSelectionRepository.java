package com.kiwi.features.conversations.controllers;

import com.kiwi.features.conversations.data.UserConversationSelectionId;
import com.kiwi.features.conversations.data.UserConversationSelectionPersistence;
import org.springframework.data.repository.Repository;

public interface UserConversationSelectionRepository
        extends Repository<UserConversationSelectionPersistence, UserConversationSelectionId> {

    UserConversationSelectionPersistence save(UserConversationSelectionPersistence selection);

    boolean existsByUserIdAndConversationId(Long userId, Long conversationId);
}
