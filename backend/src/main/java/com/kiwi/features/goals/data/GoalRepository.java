package com.kiwi.features.goals.data;

import com.kiwi.features.users.data.UsersPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<GoalPersistence, Long> {
    List<GoalPersistence> findByUserAndDate(UsersPersistence user, LocalDate date);
    
    List<GoalPersistence> findByUserOrderByDateDesc(UsersPersistence user);
    
    Optional<GoalPersistence> findByIdAndUser(Long id, UsersPersistence user);
}
