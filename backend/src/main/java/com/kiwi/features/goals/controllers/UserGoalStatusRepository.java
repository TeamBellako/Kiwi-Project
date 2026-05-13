package com.kiwi.features.goals.controllers;

import com.kiwi.features.goals.data.GoalCategory;
import com.kiwi.features.goals.data.GoalStatus;
import com.kiwi.features.goals.data.UserGoalStatusPersistence;
import com.kiwi.features.users.data.UsersPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserGoalStatusRepository extends JpaRepository<UserGoalStatusPersistence, Long> {

    List<UserGoalStatusPersistence> findByUserAndGoal_CategoryOrderByDateDesc(
            UsersPersistence user,
            GoalCategory category
    );

    List<UserGoalStatusPersistence> findByUserAndDateAndGoal_Category(
            UsersPersistence user,
            LocalDate date,
            GoalCategory category
    );

    List<UserGoalStatusPersistence> findByUserAndStatusAndDateBeforeAndGoal_CategoryOrderByDateDesc(
            UsersPersistence user,
            GoalStatus status,
            LocalDate date,
            GoalCategory category
    );

    List<UserGoalStatusPersistence> findByUserAndGoal_Category(
            UsersPersistence user,
            GoalCategory category
    );

    Optional<UserGoalStatusPersistence> findByIdAndUser(Long id, UsersPersistence user);
}
