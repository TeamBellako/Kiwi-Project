package com.kiwi.features.conversations.controllers;

import com.kiwi.features.conversations.data.ConversationOptionPersistence;
import com.kiwi.features.localization.LocalizationQueryRewriter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationOptionsRepository extends JpaRepository<ConversationOptionPersistence, Long> {

    interface LocalizedOption {
        Long getId();
        String getText();
        String getTextM();
        String getTextW();
        Long getNextEventId();
        Integer getCost();
        String getIncidenceToShow();
    }

    @Query(value = """
            SELECT id,
                   text_en AS text,
                   text_m_en AS textM,
                   text_w_en AS textW,
                   next_event_id AS nextEventId,
                   cost,
                   incidence_to_show AS incidenceToShow
            FROM conversation_options
            WHERE conversation_id = :conversationId
            """, nativeQuery = true, queryRewriter = LocalizationQueryRewriter.class)
    List<LocalizedOption> findLocalizedByConversationId(@Param("conversationId") Long conversationId);
}
