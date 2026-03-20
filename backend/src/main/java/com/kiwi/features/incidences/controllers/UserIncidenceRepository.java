package com.kiwi.features.incidences.controllers;

import com.kiwi.features.incidences.data.UserIncidenceKey;
import com.kiwi.features.incidences.data.UserIncidencePersistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserIncidenceRepository extends JpaRepository<UserIncidencePersistence, UserIncidenceKey> {
    List<UserIncidencePersistence> findByIdUserIdAndIncidenceId(Long userId, Long incidenceId);
}
