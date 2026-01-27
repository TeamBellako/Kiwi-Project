package com.kiwi.features.conversations.controllers;

import com.kiwi.features.conversations.data.*;
import com.kiwi.features.conversations.exceptions.ConversationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConversationService {
    
    private final ConversationsRepository conversationsRepository;
    private final ConversationOptionsRepository conversationOptionsRepository;
    
    public ConversationService(ConversationsRepository conversationsRepository,
                              ConversationOptionsRepository conversationOptionsRepository) {
        this.conversationsRepository = conversationsRepository;
        this.conversationOptionsRepository = conversationOptionsRepository;
    }
    
    /**
     * Obtener conversation por ID con sus options.
     */
    @Transactional(readOnly = true)
    public ConversationDTO getConversationById(Long id) {
        ConversationPersistence conversation = conversationsRepository.findById(id)
                .orElseThrow(() -> new ConversationNotFoundException(id));

        List<ConversationOptionPersistence> options = conversationOptionsRepository.findByConversationId(id);

        return ConversationMapper.toDto(conversation, options);
    }
}
