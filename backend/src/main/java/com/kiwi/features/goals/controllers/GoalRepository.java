package com.kiwi.features.goals.controllers;

import com.kiwi.features.goals.data.GoalCategory;
import com.kiwi.features.goals.data.GoalPersistence;
import com.kiwi.features.goals.data.GoalStatus;
import com.kiwi.features.users.data.UsersPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<GoalPersistence, Long> {

    List<GoalPersistence> findByUserAndCategoryNotOrderByDateDesc(
            UsersPersistence user,
            GoalCategory category
    );

    List<GoalPersistence> findByUserAndDateAndCategoryNot(
            UsersPersistence user,
            LocalDate date,
            GoalCategory category
    );

    List<GoalPersistence> findByUserAndStatusAndDateBeforeAndCategoryNotOrderByDateDesc(
            UsersPersistence user,
            GoalStatus status,
            LocalDate date,
            GoalCategory category
    );

    List<GoalPersistence> findByUserAndCategory(
            UsersPersistence user,
            GoalCategory category
    );
    
    Optional<GoalPersistence> findByIdAndUser(Long id, UsersPersistence user);
}
