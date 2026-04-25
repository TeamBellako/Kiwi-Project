package com.kiwi.features.goals.controllers;

import com.kiwi.features.goals.data.GoalCategory;
import com.kiwi.features.goals.data.GoalPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<GoalPersistence, Long> {

    List<GoalPersistence> findByCategory(GoalCategory category);

    @Query(value = """
            SELECT g.*
            FROM goals g
            LEFT JOIN user_goal_progress ugp
                ON ugp.user_id = :userId
               AND ugp.goal_type = g.type
            WHERE g.difficulty = COALESCE(ugp.current_difficulty, 1)
            ORDER BY RAND()
            LIMIT 2
            """, nativeQuery = true)
    List<GoalPersistence> findTwoRandomForUser(@Param("userId") Long userId);
}
