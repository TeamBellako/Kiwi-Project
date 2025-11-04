package com.kiwi.features.nodes.controllers;

import com.kiwi.features.nodes.data.UserNodeStatusKey;
import com.kiwi.features.nodes.data.UserNodeStatusPersistence;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserNodeStatusRepository extends JpaRepository<UserNodeStatusPersistence, UserNodeStatusKey> {
    Optional<UserNodeStatusPersistence> findByUserIdAndNodeId(@NotNull int userId, int id);
}
