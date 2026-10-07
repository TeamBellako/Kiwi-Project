package com.kiwi.features.conversations.controllers;

import com.kiwi.features.conversations.data.ConversationPersistence;
import com.kiwi.features.localization.LocalizationQueryRewriter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConversationsRepository extends JpaRepository<ConversationPersistence, Long> {

    interface LocalizedConversation {
        Long getId();
        String getType();
        String getName();
        String getSprite();
        String getBackground();
        String getFx();
        Boolean getDark();
        Integer getDelayStartMs();
        Integer getDelayEndMs();
        String getDialog();
        String getDialogM();
        String getDialogW();
        String getNextEvent();
        Long getEventId();
        String getIncidenceForNextEvent();
        Long getFallbackEventId();
        String getIncidenceNameToSet();
        Boolean getIncidenceValueToSet();
        String getOnCompletedAction();
        String getOnCompletedEntity();
        Integer getOnCompletedEntityId();
    }

    @Query(value = """
            SELECT id, type, name, sprite, background, fx, dark,
                   delay_start_ms AS delayStartMs, delay_end_ms AS delayEndMs,
                   dialog_en AS dialog, dialog_m_en AS dialogM, dialog_w_en AS dialogW,
                   next_event AS nextEvent, event_id,
                   incidence_for_next_event AS incidenceForNextEvent,
                   fallback_event_id AS fallbackEventId,
                   incidence_name_to_set AS incidenceNameToSet,
                   incidence_value_to_set AS incidenceValueToSet,
                   on_completed_action AS onCompletedAction,
                   on_completed_entity AS onCompletedEntity,
                   on_completed_entity_id AS onCompletedEntityId
            FROM conversations
            WHERE id = :id
            """, nativeQuery = true, queryRewriter = LocalizationQueryRewriter.class)
    Optional<LocalizedConversation> findLocalizedById(@Param("id") Long id);
}
