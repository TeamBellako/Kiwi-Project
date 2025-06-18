package com.kiwi.metrics;

import com.kiwi.users.UsersPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MetricsRepository extends JpaRepository<MetricsPersistence, Long> {
    Optional<MetricsPersistence> findByUserAndDate(UsersPersistence usersPersistence, LocalDate date);
}
