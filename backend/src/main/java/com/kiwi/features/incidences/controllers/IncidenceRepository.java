package com.kiwi.features.incidences.controllers;

import com.kiwi.features.incidences.data.IncidencePersistance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncidenceRepository extends JpaRepository<IncidencePersistance, Long> {
    Optional<IncidencePersistance> findByName(String name);
}
