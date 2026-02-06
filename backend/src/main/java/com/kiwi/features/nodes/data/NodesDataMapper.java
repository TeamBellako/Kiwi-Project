package com.kiwi.features.nodes.data;

import java.util.List;

public class NodesDataMapper {

    public static NodesDomain toDomain(NodesPersistence node, UserNodeStatusPersistence userStatus) {
        return NodesDomainFactory.create(node, userStatus);

    }

    public static NodesDTO toDTO(NodesDomain node) {
        return new NodesDTO(
                node.getId(),
                node.getStatus().name(),
                node.getIcon(),
                node.getPrice(),
                node.getCordX(),
                node.getCordY(),
                node.getEventOnExecution(),
                node.getName(),
                node.getDisplayName(),
                node.getConnectedNodeIds(),
                node.getMapId(),
                node.isFirstNodeOfMap()
        );
    }

    public static NodesDTO toDTO(NodesPersistence node, UserNodeStatusPersistence status) {
        List<Long> connectedNodeIds = node.getOutgoingEdges()
                .stream()
                .map(edge -> edge.getToNode().getId())
                .toList();

        return new NodesDTO(
                node.getId(),
                status.getStatus().name(),
                node.getIcon(),
                node.getPrice(),
                node.getCordX(),
                node.getCordY(),
                node.getEventOnExecution(),
                node.getName(),
                node.getDisplayName(),
                connectedNodeIds,
                node.getMapId(),
                node.isFirstNodeOfMap()
        );
    }

    public static UserNodeStatusPersistence toPersistence(Long userId, NodesDomain node) {
        if (node.getStatus() == NodeStatus.INACCESSIBLE) {
            return null;
        }

        UserNodeStatusKey key = new UserNodeStatusKey(userId, node.getId());

        UserNodeStatusPersistence entity = new UserNodeStatusPersistence();
        entity.setId(key);
        entity.setStatus(node.getStatus());
        return entity;
    }
}