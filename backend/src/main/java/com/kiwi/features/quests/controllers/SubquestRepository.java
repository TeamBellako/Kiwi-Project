package com.kiwi.features.quests.controllers;

import com.kiwi.features.localization.LocalizationQueryRewriter;
import com.kiwi.features.quests.data.SubquestPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubquestRepository extends JpaRepository<SubquestPersistence, Integer> {

    interface LocalizedSubquest {
        Integer getId();
        String getName();
    }

    List<SubquestPersistence> findAllByQuestIdOrderByOrderIndex(int questId);

    @Query(value = """
            SELECT id, name_en AS name
            FROM subquests
            WHERE id IN (:ids)
            """, nativeQuery = true, queryRewriter = LocalizationQueryRewriter.class)
    List<LocalizedSubquest> findLocalizedByIds(@Param("ids") List<Integer> ids);
}
