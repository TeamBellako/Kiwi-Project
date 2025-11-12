package com.kiwi.features.nodes.controllers;

import com.kiwi.features.nodes.data.NodesPersistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NodesRepository extends JpaRepository<NodesPersistence, Integer> {
    List<NodesPersistence> findAllByNodeOrder(int nodeOrder);
}