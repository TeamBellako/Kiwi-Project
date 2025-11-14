package com.kiwi.features.nodes.data;

public class NodesDomainFactory {

    public static NodesDomain create(NodesPersistence node, UserNodeStatusPersistence userStatus) {
        NodeStatus status = (userStatus != null)
                ? userStatus.getStatus()
                : NodeStatus.INACCESSIBLE;

        return new NodesDomain(
                node.getId(),
                node.getNodeOrder(),
                status,
                node.getPrice(),
                node.getCordX(),
                node.getCordY()
        );
    }
}