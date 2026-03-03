package com.kiwi.features.conversations.controllers;

import com.kiwi.features.conversations.data.ConversationOptionPersistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationOptionsRepository extends JpaRepository<ConversationOptionPersistence, Long> {
    List<ConversationOptionPersistence> findByConversationId(Long conversationId);
}
