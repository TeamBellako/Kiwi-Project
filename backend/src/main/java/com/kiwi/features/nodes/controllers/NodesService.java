package com.kiwi.features.nodes.controllers;

import com.kiwi.features.nodes.data.*;
import com.kiwi.features.nodes.exceptions.NodeMarkAsLockedException;
import com.kiwi.features.nodes.exceptions.NodeNotFoundException;
import com.kiwi.features.nodes.exceptions.NodeLockedException;
import com.kiwi.features.nodes.exceptions.NodeSatusNotFoundException;
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

    public List<NodesDTO> getNodesForUser(@NotNull int userId) {
        List<NodesPersistence> nodes = nodesRepository.findAll();

        return nodes.stream().map(n -> {
            UserNodeStatusPersistence userStatus = userNodeStatusRepository
                    .findByIdUserIdAndIdNodeId(userId, n.getId())
                    .orElse(null);

            NodesDomain domain = NodesDomainFactory.create(n, userStatus);
            return NodesDataMapper.toDTO(domain);

        }).collect(Collectors.toList());
    }

    @Transactional
    public List<NodesDTO> markNextNodesAsLocked(int userId, int nodeId) {
        NodesPersistence node = nodesRepository.findById(nodeId)
                .orElseThrow(() -> new NodeNotFoundException(nodeId));

        UserNodeStatusPersistence nodeStatusPersistence = userNodeStatusRepository
                .findByIdUserIdAndIdNodeId(userId, nodeId)
                .orElseThrow(() -> new NodeSatusNotFoundException(nodeId));

        if(nodeStatusPersistence.getStatus() != NodeStatus.COMPLETED){
            throw new NodeMarkAsLockedException(nodeId);
        }

        int nextOrder = node.getNodeOrder() + 1;

        List<NodesPersistence> nodes = nodesRepository.findAllByNodeOrder(nextOrder);

        return nodes.stream().map(n -> {
            NodesDomain domain = NodesDataMapper.toDomain(n, null);
            NodesDomain locked = progressService.lock(domain);

            UserNodeStatusPersistence persistence = NodesDataMapper.toPersistence(userId, locked);
            userNodeStatusRepository.saveAndFlush(persistence);
            return NodesDataMapper.toDTO(n, persistence);

        }).collect(Collectors.toList());
    }

    @Transactional
    public NodesDTO unlockNode(int userId, int nodeId) {
        NodesPersistence node = nodesRepository.findById(nodeId)
                .orElseThrow(() -> new NodeNotFoundException(nodeId));

        UserNodeStatusPersistence current = userNodeStatusRepository
                .findByIdUserIdAndIdNodeId(userId, nodeId)
                .orElseThrow(() -> new NodeLockedException(nodeId));

        NodesDomain domain = NodesDataMapper.toDomain(node, current);
        NodesDomain opened = progressService.unlock(domain);

        UserNodeStatusPersistence persistence = NodesDataMapper.toPersistence(userId, opened);

        userNodeStatusRepository.saveAndFlush(persistence);
        return NodesDataMapper.toDTO(node, persistence);
    }

    @Transactional
    public NodesDTO completeNode(int userId, int nodeId) {
        NodesPersistence node = nodesRepository.findById(nodeId)
                .orElseThrow(() -> new NodeNotFoundException(nodeId));

        UserNodeStatusPersistence current = userNodeStatusRepository
                .findByIdUserIdAndIdNodeId(userId, nodeId)
                .orElseThrow(() -> new NodeLockedException(nodeId));

        NodesDomain domain = NodesDataMapper.toDomain(node, current);
        NodesDomain completed = progressService.complete(domain);

        UserNodeStatusPersistence persistence = NodesDataMapper.toPersistence(userId, completed);
        userNodeStatusRepository.saveAndFlush(persistence);
        return NodesDataMapper.toDTO(node, persistence);
    }


    public void initializeUserProgress(int userId) {
        NodesPersistence firstNode = nodesRepository.findById(1)
                .orElseThrow(() -> new NodeNotFoundException(1));

        NodesDomain domain = NodesDataMapper.toDomain(firstNode, null);
        NodesDomain locked = progressService.lock(domain);

        UserNodeStatusPersistence persistence = NodesDataMapper.toPersistence(userId, locked);
        userNodeStatusRepository.saveAndFlush(persistence);
    }
}