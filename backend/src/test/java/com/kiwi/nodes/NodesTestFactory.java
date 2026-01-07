package com.kiwi.nodes;

import com.kiwi.features.nodes.data.*;

import java.util.List;

public class NodesTestFactory {

    public static NodesPersistence persistenceNode(Long nodeId) {
        NodesPersistence n = new NodesPersistence();
        n.setPrice(0);
        n.setCordX(0);
        n.setCordY(0);
        n.setEventOnExecution(0L);
        n.setName("Node"+nodeId);
        return n;
    }

    public static void connectNodes(
            NodesPersistence from,
            NodesPersistence... toNodes
    ) {
        for (NodesPersistence to : toNodes) {
            NodeEdgePersistence edge = NodeEdgePersistence.builder()
                    .fromNode(from)
                    .toNode(to)
                    .build();

            from.getOutgoingEdges().add(edge);
        }
    }

    public static UserNodeStatusPersistence persistenceStatus(
            Long userId, Long nodeId, NodeStatus status
    ) {
        UserNodeStatusKey key = new UserNodeStatusKey(userId, nodeId);
        UserNodeStatusPersistence s = new UserNodeStatusPersistence();
        s.setId(key);
        s.setStatus(status);
        return s;
    }

    // Helpers
    public static UserNodeStatusPersistence inaccessibleStatus(Long userId, Long nodeId) {
        return persistenceStatus(userId, nodeId, NodeStatus.INACCESSIBLE);
    }

    public static UserNodeStatusPersistence lockedStatus(Long userId, Long nodeId) {
        return persistenceStatus(userId, nodeId, NodeStatus.LOCKED);
    }

    public static UserNodeStatusPersistence openStatus(Long userId, Long nodeId) {
        return persistenceStatus(userId, nodeId, NodeStatus.OPEN);
    }

    public static UserNodeStatusPersistence completeStatus(Long userId, Long nodeId) {
        return persistenceStatus(userId, nodeId, NodeStatus.COMPLETED);
    }

    public static NodesDTO dto(Long id, NodeStatus status) {
        return new NodesDTO(
                id,
                status.name(),
                0,
                100,
                0f,
                0f,
                null,
                "node" + id,
                "Node " + id,
                List.of()
        );
    }
}
