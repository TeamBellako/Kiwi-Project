package com.kiwi.features.nodes.data;

import java.util.List;

public class NodesDomainFactory {

    public static NodesDomain create(NodesPersistence node, UserNodeStatusPersistence userStatus) {
        NodeStatus status = (userStatus != null)
                ? userStatus.getStatus()
                : NodeStatus.INACCESSIBLE;

        List<Long> connectedNodeIds = node.getOutgoingEdges()
                .stream()
                .map(edge -> edge.getToNode().getId())
                .toList();

        return new NodesDomain(
                node.getId(),
                status,
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
}