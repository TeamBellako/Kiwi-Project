package com.kiwi.features.nodes.data;

import java.util.List;
import java.util.stream.Collectors;

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
                node.getPrice(),
                node.getCordX(),
                node.getCordY(),
                node.getEventOnExecution(),
                node.getName(),
                node.getDisplayName(),
                connectedNodeIds
        );
    }
}