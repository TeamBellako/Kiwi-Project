package com.kiwi.features.metrics.controllers;

import com.kiwi.features.metrics.data.MetricsPersistence;
import com.kiwi.features.users.data.UsersPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MetricsRepository extends JpaRepository<MetricsPersistence, Long> {
    Optional<MetricsPersistence> findByUserAndDate(UsersPersistence usersPersistence, LocalDate date);
}
