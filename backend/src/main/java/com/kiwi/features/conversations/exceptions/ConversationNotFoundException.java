package com.kiwi.features.conversations.exceptions;

public class ConversationNotFoundException extends RuntimeException {
    public ConversationNotFoundException(Long id) {
        super(String.format("Conversation with id %d not found", id));
    }
}
