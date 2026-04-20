package com.kiwi.features.conversations.data;

public class ConversationOptionMapper {

    /**
     * Convierte ConversationOptionPersistence a DTO.
     */
    public static ConversationOptionDTO toDto(ConversationOptionPersistence entity) {
        if (entity == null) return null;
        return ConversationOptionDTO.builder()
                .id(entity.getId())
                .conversationId(entity.getConversation() != null ? entity.getConversation().getId() : null)
                .text(entity.getText())
                .textM(entity.getTextM())
                .textW(entity.getTextW())
                .nextEventId(entity.getNextEventId())
                .incidenceToShow(entity.getIncidenceToShow())
            .cost(entity.getCost())
                .build();
    }

    /**
     * Convierte ConversationOptionDTO a entidad.
     * NOTA: No establece la referencia a Conversation aquí.
     * Debe ser establecida por el caller (ConversationMapper).
     */
    public static ConversationOptionPersistence toEntity(ConversationOptionDTO dto) {
        if (dto == null) return null;
        
        ConversationOptionPersistence entity = new ConversationOptionPersistence();
        entity.setId(dto.getId());
        entity.setText(dto.getText());
        entity.setTextM(dto.getTextM());
        entity.setTextW(dto.getTextW());
        entity.setNextEventId(dto.getNextEventId());
        entity.setCost(dto.getCost());
        entity.setIncidenceToShow(dto.getIncidenceToShow());
        return entity;
    }
}
