package com.kiwi.features.nodes.controllers;

import com.kiwi.features.nodes.data.UserNodeStatusKey;
import com.kiwi.features.nodes.data.UserNodeStatusPersistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserNodeStatusRepository extends JpaRepository<UserNodeStatusPersistence, UserNodeStatusKey> {
    Optional<UserNodeStatusPersistence> findByIdUserIdAndIdNodeId(Long userId, int nodeId);
}
