package com.kiwi.features.incidences.controllers;

import com.kiwi.features.incidences.data.IncidencePersistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncidenceRepository extends JpaRepository<IncidencePersistence, Long> {
    Optional<IncidencePersistence> findByName(String name);
}
