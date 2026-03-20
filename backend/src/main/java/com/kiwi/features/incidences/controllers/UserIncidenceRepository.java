package com.kiwi.features.incidences.controllers;

import com.kiwi.features.incidences.data.UserIncidenceKey;
import com.kiwi.features.incidences.data.UserIncidencePersistance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserIncidenceRepository extends JpaRepository<UserIncidencePersistance, UserIncidenceKey> {
    List<UserIncidencePersistance> findByIdUserIdAndIncidenceId(Long userId, Long incidenceId);
}
