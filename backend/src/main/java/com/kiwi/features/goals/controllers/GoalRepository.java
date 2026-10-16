package com.kiwi.features.goals.controllers;

import com.kiwi.features.goals.data.GoalCategory;
import com.kiwi.features.goals.data.GoalPersistence;
import com.kiwi.features.localization.LocalizationQueryRewriter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<GoalPersistence, Long> {

    interface LocalizedGoal {
        Long getId();
        String getName();
        String getAction();
        Integer getTarget();
        String getType();
        String getCategory();
        Integer getReward();
        Integer getDifficulty();
        String getOnCompletedAction();
        String getOnCompletedEntity();
        Integer getOnCompletedEntityId();
    }

    List<GoalPersistence> findByCategory(GoalCategory category);

        @Query(value = """
            SELECT g.id, g.name, COALESCE(g.action_en, g.name) AS action, g.target, g.type, g.category,
               g.reward, g.difficulty, g.on_completed_action AS onCompletedAction,
               g.on_completed_entity AS onCompletedEntity,
               g.on_completed_entity_id AS onCompletedEntityId
            FROM goals g
            LEFT JOIN user_goal_progress ugp
                ON ugp.user_id = :userId
               AND ugp.goal_type = g.type
            WHERE g.category = 'DAILY_CHALLENGES'
              AND g.difficulty = COALESCE(ugp.current_difficulty, 1)
            ORDER BY RAND()
            LIMIT 2
            """, nativeQuery = true, queryRewriter = LocalizationQueryRewriter.class)
    List<LocalizedGoal> findTwoRandomForUser(@Param("userId") Long userId);

    // Proyección y no entidad: la query nativa devolvería la entidad ya cargada con el texto genérico.
    @Query(value = """
            SELECT id, name, COALESCE(action_en, name) AS action
            FROM goals
            WHERE id IN (:ids)
            """, nativeQuery = true, queryRewriter = LocalizationQueryRewriter.class)
    List<LocalizedGoal> findLocalizedByIds(@Param("ids") List<Long> ids);
}
