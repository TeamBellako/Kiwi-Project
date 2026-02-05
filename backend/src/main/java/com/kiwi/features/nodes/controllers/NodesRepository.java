package com.kiwi.features.nodes.controllers;

import com.kiwi.features.nodes.data.NodesPersistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NodesRepository extends JpaRepository<NodesPersistence, Long> {
}