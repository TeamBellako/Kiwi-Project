package com.kiwi.features.nodes.controllers;

import com.kiwi.features.nodes.data.*;
import com.kiwi.features.nodes.exceptions.NodeNotFoundException;
import com.kiwi.features.nodes.exceptions.NodeLockedException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NodesService {

    private final NodesRepository nodesRepository;
    private final UserNodeStatusRepository userNodeStatusRepository;
    private final NodesProgressService progressService;

    @Autowired
    public NodesService(
            NodesRepository nodesRepository,
            UserNodeStatusRepository userNodeStatusRepository,
            NodesProgressService progressService
    ) {
        this.nodesRepository = nodesRepository;
        this.userNodeStatusRepository = userNodeStatusRepository;
        this.progressService = progressService;
    }

    public NodesDTO getNode(int nodeId) {
        NodesPersistence node = nodesRepository.findById(nodeId)
                .orElseThrow(() -> new NodeNotFoundException(nodeId));
        return NodesDataMapper.toDTO(node, null);

    }

    public NodesDTO getNodeForUser(int userId, int nodeId) {
        NodesPersistence node = nodesRepository.findById(nodeId)
                .orElseThrow(() -> new NodeNotFoundException(nodeId));

        UserNodeStatusPersistence userStatus = userNodeStatusRepository
                .findByUserIdAndNodeId(userId, node.getId())
                .orElse(null);

        if (userStatus != null) {
            NodesDomain domain = NodesDomainFactory.create(node, userStatus);
            return NodesDataMapper.toDTO(domain);
        }
        else {
            return NodesDataMapper.toDTO(node, null);
        }

    }

    public List<NodesDTO> getNodesForUser(@NotNull int userId) {
        List<NodesPersistence> nodes = nodesRepository.findAll();

        return nodes.stream().map(n -> {
            UserNodeStatusPersistence userStatus = userNodeStatusRepository
                    .findByUserIdAndNodeId(userId, n.getId())
                    .orElse(null);

            NodesDomain domain = NodesDomainFactory.create(n, userStatus);
            return NodesDataMapper.toDTO(domain);

        }).collect(Collectors.toList());
    }

    @Transactional
    public NodesDTO markNextNodeAsLocked(int userId, int nodeId) {
        NodesPersistence node = nodesRepository.findById(nodeId)
                .orElseThrow(() -> new NodeNotFoundException(nodeId));

        if (userNodeStatusRepository.findByUserIdAndNodeId(userId, nodeId).isPresent()) {
            return null;
        }

        NodesDomain domain = NodesDataMapper.toDomain(node, null);
        NodesDomain locked = progressService.lock(domain);

        UserNodeStatusPersistence persistence = NodesDataMapper.toPersistence(userId, locked);
        userNodeStatusRepository.save(persistence);
        return NodesDataMapper.toDTO(node, persistence);
    }

    @Transactional
    public NodesDTO unlockNode(int userId, int nodeId) {
        NodesPersistence node = nodesRepository.findById(nodeId)
                .orElseThrow(() -> new NodeNotFoundException(nodeId));

        UserNodeStatusPersistence current = userNodeStatusRepository
                .findByUserIdAndNodeId(userId, nodeId)
                .orElseThrow(() -> new NodeLockedException(nodeId));

        NodesDomain domain = NodesDataMapper.toDomain(node, current);
        NodesDomain opened = progressService.unlock(domain);

        UserNodeStatusPersistence persistence = NodesDataMapper.toPersistence(userId, opened);

        userNodeStatusRepository.save(persistence);
        return NodesDataMapper.toDTO(node, persistence);
    }

    @Transactional
    public NodesDTO completeNode(int userId, int nodeId) {
        NodesPersistence node = nodesRepository.findById(nodeId)
                .orElseThrow(() -> new NodeNotFoundException(nodeId));

        UserNodeStatusPersistence current = userNodeStatusRepository
                .findByUserIdAndNodeId(userId, nodeId)
                .orElseThrow(() -> new NodeLockedException(nodeId));

        NodesDomain domain = NodesDataMapper.toDomain(node, current);
        NodesDomain completed = progressService.complete(domain);

        UserNodeStatusPersistence persistence = NodesDataMapper.toPersistence(userId, completed);
        userNodeStatusRepository.save(persistence);
        return NodesDataMapper.toDTO(node, persistence);
    }



}