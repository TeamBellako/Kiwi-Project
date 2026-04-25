package com.kiwi.features.goals.controllers;

import com.kiwi.features.goals.data.UserGoalProgressKey;
import com.kiwi.features.goals.data.UserGoalProgressPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGoalProgressRepository extends JpaRepository<UserGoalProgressPersistence, UserGoalProgressKey> {
}