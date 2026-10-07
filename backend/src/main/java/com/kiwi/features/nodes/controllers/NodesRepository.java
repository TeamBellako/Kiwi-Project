package com.kiwi.features.nodes.controllers;

import com.kiwi.features.localization.LocalizationQueryRewriter;
import com.kiwi.features.nodes.data.NodesPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NodesRepository extends JpaRepository<NodesPersistence, Long> {

    interface LocalizedNode {
        Long getId();
        String getDisplayName();
    }

    List<NodesPersistence> findByIsFirstNodeOfMapTrue();

    @Query(value = """
            SELECT id, COALESCE(display_name_en, '') AS displayName
            FROM nodes
            WHERE id IN (:ids)
            """, nativeQuery = true, queryRewriter = LocalizationQueryRewriter.class)
    List<LocalizedNode> findLocalizedByIds(@Param("ids") List<Long> ids);
}