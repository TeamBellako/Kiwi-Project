package com.kiwi.features.goals.controllers;

import com.kiwi.features.goals.data.SuggestedGoalPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestedGoalRepository extends JpaRepository<SuggestedGoalPersistence, String> {
}
