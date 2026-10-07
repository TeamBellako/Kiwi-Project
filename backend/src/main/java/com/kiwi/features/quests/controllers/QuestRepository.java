package com.kiwi.features.quests.controllers;

import com.kiwi.features.localization.LocalizationQueryRewriter;
import com.kiwi.features.quests.data.QuestPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestRepository extends JpaRepository<QuestPersistence, Integer> {

    interface LocalizedQuest {
        Integer getId();
        String getName();
        String getDescription();
    }

        // Proyección para que la consulta localizada no reutilice entidades gestionadas.
    @Query(value = """
            SELECT id,
                 name_en AS name,
                 description_en AS description
            FROM quests
            WHERE id IN (:ids)
            """, nativeQuery = true, queryRewriter = LocalizationQueryRewriter.class)
    List<LocalizedQuest> findLocalizedByIds(@Param("ids") List<Integer> ids);
}
