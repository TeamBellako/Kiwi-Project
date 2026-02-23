package com.kiwi.features.nodes.controllers;

import com.kiwi.features.nodes.data.*;
import com.kiwi.features.nodes.exceptions.NodeInaccessibleException;
import com.kiwi.features.nodes.exceptions.NodeNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public List<NodesDTO> getNodesForMapId(@NotNull int mapId, @NotNull Long userId) {
        List<NodesDTO> allNodes = getNodesForUser(userId);
        return allNodes.stream().filter(node -> Objects.equals(node.getMapId(), mapId)).toList();
    }

    public List<NodesDTO> getNodesForUser(@NotNull Long userId) {
        List<UserNodeStatusPersistence> statuses =
                userNodeStatusRepository.findByIdUserId(userId);

        return statuses.stream()
                .map(status -> {
                    NodesPersistence node = nodesRepository.findById(status.getId().getNodeId())
                            .orElseThrow(() -> new NodeNotFoundException(
                                    status.getId().getNodeId()
                            ));

                    NodesDomain domain =
                            NodesDomainFactory.create(node, status);

                    return NodesDataMapper.toDTO(domain);
                })
                .toList();
    }

    //FOR FUTURE CONDITION EDGES
    @Transactional
    public NodesDTO lockNode(Long userId, Long nodeId) {

        NodesPersistence node = nodesRepository.findById(nodeId)
                .orElseThrow(() -> new NodeNotFoundException(nodeId));

        UserNodeStatusPersistence existingStatus =
                userNodeStatusRepository
                        .findByIdUserIdAndIdNodeId(userId, nodeId)
                        .orElse(null);

        NodesDomain domain = NodesDataMapper.toDomain(node, existingStatus);

        NodesDomain locked = progressService.lock(domain);

        UserNodeStatusPersistence persistence =
                NodesDataMapper.toPersistence(userId, locked);

        userNodeStatusRepository.saveAndFlush(persistence);

        return NodesDataMapper.toDTO(node, persistence);
    }


    @Transactional
    public NodesDTO unlockNode(Long userId, Long nodeId) {
        NodesPersistence node = nodesRepository.findById(nodeId)
                .orElseThrow(() -> new NodeNotFoundException(nodeId));

        UserNodeStatusPersistence current = userNodeStatusRepository
                .findByIdUserIdAndIdNodeId(userId, nodeId)
                .orElseThrow(() -> new NodeInaccessibleException(nodeId));

        NodesDomain domain = NodesDataMapper.toDomain(node, current);
        NodesDomain opened = progressService.unlock(domain);

        UserNodeStatusPersistence persistence = NodesDataMapper.toPersistence(userId, opened);

        userNodeStatusRepository.saveAndFlush(persistence);
        return NodesDataMapper.toDTO(node, persistence);
    }

    @Transactional
    public List<NodesDTO> completeNode(Long userId, Long nodeId) {

        NodesPersistence node = nodesRepository.findById(nodeId)
                .orElseThrow(() -> new NodeNotFoundException(nodeId));

        UserNodeStatusPersistence current = userNodeStatusRepository
                .findByIdUserIdAndIdNodeId(userId, nodeId)
                .orElseThrow(() -> new NodeInaccessibleException(nodeId));

        NodesDomain domain = NodesDataMapper.toDomain(node, current);
        NodesDomain completed = progressService.complete(domain);

        UserNodeStatusPersistence completedPersistence =
                NodesDataMapper.toPersistence(userId, completed);

        userNodeStatusRepository.saveAndFlush(completedPersistence);

        List<NodesDTO> result = new ArrayList<>();

        result.add(NodesDataMapper.toDTO(completed));

        node.getOutgoingEdges()
                .stream()
                .map(edge -> edge.getToNode().getId())
                .map(nextNodeId -> lockNode(userId, nextNodeId))
                .forEach(result::add);

        return result;
    }

    public void initializeUserProgress(Long userId) {
        List<NodesPersistence> firstNodes = nodesRepository.findByIsFirstNodeOfMapTrue();

        if (firstNodes.isEmpty()) {
            throw new NodeNotFoundException(userId);
        }

        for (NodesPersistence firstNode : firstNodes) {
            NodesDomain domain = NodesDataMapper.toDomain(firstNode, null);

            NodesDomain locked = progressService.lock(domain);

            UserNodeStatusPersistence persistence = NodesDataMapper.toPersistence(userId, locked);
            userNodeStatusRepository.saveAndFlush(persistence);
        }
    }
}