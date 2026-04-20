package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.persistence.UserStatsPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStatsPersistence, Long> {
}