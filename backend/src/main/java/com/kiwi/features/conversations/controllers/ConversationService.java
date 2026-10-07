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
        ConversationsRepository.LocalizedConversation localized = conversationsRepository.findLocalizedById(id)
                .orElseThrow(() -> new ConversationNotFoundException(id));
        ConversationPersistence conversation = ConversationPersistence.builder()
            .id(localized.getId())
            .type(ConversationType.valueOf(localized.getType()))
            .name(localized.getName())
            .sprite(localized.getSprite())
            .background(localized.getBackground())
            .fx(localized.getFx())
            .dark(localized.getDark())
            .delayStartMs(localized.getDelayStartMs())
            .delayEndMs(localized.getDelayEndMs())
            .dialog(localized.getDialog())
            .dialogM(localized.getDialogM())
            .dialogW(localized.getDialogW())
            .nextEvent(NextEventType.valueOf(localized.getNextEvent()))
            .eventId(localized.getEventId())
            .incidenceForNextEvent(localized.getIncidenceForNextEvent())
            .fallbackEventId(localized.getFallbackEventId())
            .incidenceNameToSet(localized.getIncidenceNameToSet())
            .incidenceValueToSet(localized.getIncidenceValueToSet())
            .onCompletedAction(localized.getOnCompletedAction())
            .onCompletedEntity(localized.getOnCompletedEntity())
            .onCompletedEntityId(localized.getOnCompletedEntityId())
            .build();

        List<ConversationOptionDTO> localizedOptions = conversationOptionsRepository.findLocalizedByConversationId(id)
            .stream()
            .map(option -> ConversationOptionDTO.builder()
                .id(option.getId())
                .conversationId(id)
                .text(option.getText())
                .textM(option.getTextM())
                .textW(option.getTextW())
                .nextEventId(option.getNextEventId())
                .cost(option.getCost())
                .incidenceToShow(option.getIncidenceToShow())
                .build())
            .toList();

        ConversationDTO dto = ConversationMapper.toDto(conversation, List.of());
        dto.setOptions(localizedOptions);
        return dto;
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
