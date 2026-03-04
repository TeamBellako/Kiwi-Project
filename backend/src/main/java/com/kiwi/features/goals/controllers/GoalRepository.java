package com.kiwi.features.goals.controllers;

import com.kiwi.features.goals.data.GoalCategory;
import com.kiwi.features.goals.data.GoalPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<GoalPersistence, Long> {

    List<GoalPersistence> findByCategory(GoalCategory category);
}
