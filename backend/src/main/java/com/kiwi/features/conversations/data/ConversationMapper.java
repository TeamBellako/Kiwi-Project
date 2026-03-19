package com.kiwi.features.conversations.data;

import com.kiwi.features.sprites.data.ExpressionPersistence;
import com.kiwi.features.sprites.data.SpritePersistence;
import com.kiwi.features.sprites.data.BackgroundPersistence;
import com.kiwi.features.sprites.data.FxPersistence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConversationMapper {

    /**
     * Convierte ConversationPersistence a DTO.
     * La lista de options debe ser pasada por el caller (obtenida del repositorio por conversationId).
     * Si options es null, se asigna una lista vacía.
     */
    public static ConversationDTO toDto(ConversationPersistence entity, List<ConversationOptionPersistence> options) {
        if (entity == null) return null;
        
        List<ConversationOptionDTO> optionDtos = (options != null) 
            ? options.stream().map(ConversationOptionMapper::toDto).collect(Collectors.toList())
            : new ArrayList<>();
        
        String onCompletedEvent = entity.getOnCompletedAction() + '_' + entity.getOnCompletedEntity();
        
        return ConversationDTO.builder()
            .id(entity.getId())
            .name(entity.getName())
            .type(entity.getType())
            .spriteId(entity.getSprite() != null ? entity.getSprite().getId() : null)
            .expresionId(entity.getExpresion() != null ? entity.getExpresion().getId() : null)
            .backgroundId(entity.getBackground() != null ? entity.getBackground().getId() : null)
            .fxId(entity.getFx() != null ? entity.getFx().getId() : null)
            .dark(entity.getDark())
            .delayStartMs(entity.getDelayStartMs())
            .delayEndMs(entity.getDelayEndMs())
            .dialog(entity.getDialog())
            .dialogM(entity.getDialogM())
            .dialogW(entity.getDialogW())
            .nextEvent(entity.getNextEvent())
            .eventId(entity.getEventId())
            .incidenceForNextEvent(entity.getIncidenceForNextEvent())
            .fallbackEventId(entity.getFallbackEventId())
            .options(optionDtos)
            .onCompletedEvent(onCompletedEvent)
            .onCompletedAction(entity.getOnCompletedAction())
            .onCompletedEntity(entity.getOnCompletedEntity())
            .onCompletedEntityId(entity.getOnCompletedEntityId())
            .build();
    }

    /**
     * Convierte ConversationDTO a entidad.
     * Las options del DTO se convierten y se establece la relación bidireccional.
     * Retorna tanto la conversation como las options en una lista separada para guardar.
     */
    public static ConversationPersistence toEntity(ConversationDTO dto, List<ConversationOptionPersistence> outOptions) {
        if (dto == null) return null;
        
        ConversationPersistence entity = new ConversationPersistence();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setType(dto.getType());
        entity.setDialog(dto.getDialog());
        entity.setDialogM(dto.getDialogM());
        entity.setDialogW(dto.getDialogW());
        entity.setNextEvent(dto.getNextEvent());
        entity.setEventId(dto.getEventId());
        
        entity.setIncidenceForNextEvent(dto.getIncidenceForNextEvent());
        entity.setFallbackEventId(dto.getFallbackEventId());

        entity.setOnCompletedAction(dto.getOnCompletedAction());
        entity.setOnCompletedEntity(dto.getOnCompletedEntity());
        entity.setOnCompletedEntityId(dto.getOnCompletedEntityId());
        
        if (dto.getSpriteId() != null) {
            SpritePersistence sprite = new SpritePersistence();
            sprite.setId(dto.getSpriteId());
            entity.setSprite(sprite);
        }
        
        if (dto.getExpresionId() != null) {
            ExpressionPersistence expression = new ExpressionPersistence();
            expression.setId(dto.getExpresionId());
            entity.setExpresion(expression);
        }
        
        if (dto.getBackgroundId() != null) {
            BackgroundPersistence background = new BackgroundPersistence();
            background.setId(dto.getBackgroundId());
            entity.setBackground(background);
        }
        
        if (dto.getFxId() != null) {
            FxPersistence fx = new FxPersistence();
            fx.setId(dto.getFxId());
            entity.setFx(fx);
        }
        entity.setDark(dto.getDark());

        entity.setDelayStartMs(dto.getDelayStartMs());
        entity.setDelayEndMs(dto.getDelayEndMs());
        
        // Procesar options si existen
        if (dto.getOptions() != null && outOptions != null) {
            dto.getOptions().forEach(optionDto -> {
                ConversationOptionPersistence option = ConversationOptionMapper.toEntity(optionDto);
                option.setConversation(entity); // Relación: option -> conversation
                outOptions.add(option);
            });
        }
        
        return entity;
    }
}
