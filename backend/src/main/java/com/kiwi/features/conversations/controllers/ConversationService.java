package com.kiwi.features.conversations.controllers;

import com.kiwi.features.conversations.data.*;
import com.kiwi.features.conversations.exceptions.ConversationNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConversationService {
    
    private final ConversationsRepository conversationsRepository;
    private final ConversationOptionsRepository conversationOptionsRepository;
    private final UserConversationSelectionRepository userConversationSelectionRepository;
    
    public ConversationService(ConversationsRepository conversationsRepository,
                              ConversationOptionsRepository conversationOptionsRepository,
                              UserConversationSelectionRepository userConversationSelectionRepository) {
        this.conversationsRepository = conversationsRepository;
        this.conversationOptionsRepository = conversationOptionsRepository;
        this.userConversationSelectionRepository = userConversationSelectionRepository;
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
    
    /**
     * Guardar la selección de opciones de conversación de un usuario.
     * Si cualquiera de las opciones ya tiene una selección guardada, se omite el guardado completo.
     */
    @Transactional
    public void saveConversationOptions(Long userId, List<Long> optionIds) {
        List<ConversationOptionPersistence> options = optionIds.stream()
                .map(optionId -> conversationOptionsRepository.findById(optionId)
                        .orElseThrow(() -> new EntityNotFoundException("ConversationOption not found with id: " + optionId)))
                .toList();

        boolean anyAlreadyExists = options.stream()
                .anyMatch(option -> userConversationSelectionRepository
                        .existsByUserIdAndConversationId(userId, option.getConversation().getId()));

        if (anyAlreadyExists) {
            return;
        }

        options.forEach(option -> {
            UserConversationSelectionPersistence selection = UserConversationSelectionPersistence.builder()
                    .userId(userId)
                    .conversationId(option.getConversation().getId())
                    .optionId(option.getId())
                    .build();
            userConversationSelectionRepository.save(selection);
        });
    }
}
