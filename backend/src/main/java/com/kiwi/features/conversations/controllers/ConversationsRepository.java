package com.kiwi.features.conversations.controllers;

import com.kiwi.features.conversations.data.ConversationPersistence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationsRepository extends JpaRepository<ConversationPersistence, Long> {

}
