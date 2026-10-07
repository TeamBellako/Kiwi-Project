package com.kiwi.features.nodes.controllers;

import com.kiwi.features.nodes.events.NodeUnlockedEvent;
import com.kiwi.features.users.events.UserCreatedEvent;
import com.kiwi.features.nodes.data.*;
import com.kiwi.features.nodes.exceptions.NodeInaccessibleException;
import com.kiwi.features.nodes.exceptions.NodeNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NodesService {

    private final NodesRepository nodesRepository;
    private final UserNodeStatusRepository userNodeStatusRepository;
    private final NodesProgressService progressService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public NodesService(
            NodesRepository nodesRepository,
            UserNodeStatusRepository userNodeStatusRepository,
            NodesProgressService progressService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.nodesRepository = nodesRepository;
        this.userNodeStatusRepository = userNodeStatusRepository;
        this.progressService = progressService;
        this.eventPublisher = eventPublisher;
    }

    public List<NodesDTO> getNodesForMapId(@NotNull int mapId, @NotNull Long userId) {
        List<NodesDTO> allNodes = getNodesForUser(userId);
        return allNodes.stream().filter(node -> Objects.equals(node.getMapId(), mapId)).toList();
    }

    public List<NodesDTO> getNodesForUser(@NotNull Long userId) {
        List<UserNodeStatusPersistence> statuses =
                userNodeStatusRepository.findByIdUserId(userId);

        List<NodesDTO> nodes = statuses.stream()
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
        return localizeDisplayNames(nodes);
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

        return localizeDisplayNames(List.of(NodesDataMapper.toDTO(node, persistence))).get(0);
    }


    @Transactional
    public List<NodesDTO> unlockNode(Long userId, Long nodeId) {
        NodesPersistence node = nodesRepository.findById(nodeId)
                .orElseThrow(() -> new NodeNotFoundException(nodeId));

        UserNodeStatusPersistence current = userNodeStatusRepository
                .findByIdUserIdAndIdNodeId(userId, nodeId)
                .orElseThrow(() -> new NodeInaccessibleException(nodeId));

        NodesDomain domain = NodesDataMapper.toDomain(node, current);
        NodesDomain opened = progressService.unlock(domain);

        UserNodeStatusPersistence persistence = NodesDataMapper.toPersistence(userId, opened);

        userNodeStatusRepository.saveAndFlush(persistence);
        
        // For blank nodes, we want to automatically complete them so that edges are unlocked
        if (domain.getOnExecutionEvent().equals("_"))
        {
            return completeNode(userId, nodeId);
        }
        
        List<NodesDTO> result = new ArrayList<>();
        result.add(NodesDataMapper.toDTO(node, persistence));
        
        return localizeDisplayNames(result);
    }

    @Transactional
    public List<NodesDTO> completeNode(Long userId, Long nodeId) {

        NodesPersistence node = nodesRepository.findById(nodeId)
                .orElseThrow(() -> new NodeNotFoundException(nodeId));

        UserNodeStatusPersistence current = userNodeStatusRepository
                .findByIdUserIdAndIdNodeId(userId, nodeId)
                .orElseThrow(() -> new NodeInaccessibleException(nodeId));

        NodesDomain domain = NodesDataMapper.toDomain(node, current);

        if (domain.getStatus() == NodeStatus.COMPLETED) {
            return new ArrayList<>();
        }

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

        return localizeDisplayNames(result);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void onUserCreated(UserCreatedEvent event) {
        initializeUserProgress(event.userId());
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

        private List<NodesDTO> localizeDisplayNames(List<NodesDTO> nodes) {
                if (nodes == null || nodes.isEmpty()) {
                        return nodes;
                }

                List<Long> ids = nodes.stream().map(NodesDTO::getId).distinct().toList();
                Map<Long, NodesRepository.LocalizedNode> localizedById = nodesRepository.findLocalizedByIds(ids).stream()
                                .collect(Collectors.toMap(NodesRepository.LocalizedNode::getId, Function.identity()));

                nodes.forEach(node -> {
                        NodesRepository.LocalizedNode localized = localizedById.get(node.getId());
                        if (localized != null) {
                                node.setDisplayName(localized.getDisplayName());
                        }
                });
                return nodes;
        }
}