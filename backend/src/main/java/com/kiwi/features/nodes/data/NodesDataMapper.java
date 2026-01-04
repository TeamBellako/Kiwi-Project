package com.kiwi.features.nodes.data;

public class NodesDataMapper {

    public static NodesDomain toDomain(NodesPersistence node, UserNodeStatusPersistence userStatus) {
        return NodesDomainFactory.create(node, userStatus);

    }

    public static NodesDTO toDTO(NodesDomain node) {
        return new NodesDTO(
                node.getId(),
                node.getNodeOrder(),
                node.getStatus().name(),
                node.getPrice(),
                node.getCordX(),
                node.getCordY(),
                node.getEventOnExecution(),
                node.getName(),
                node.getDisplayName()
        );
    }

    public static NodesDTO toDTO(NodesPersistence node, UserNodeStatusPersistence status) {
        return new NodesDTO(
                node.getId(),
                node.getNodeOrder(),
                status.getStatus().name(),
                node.getPrice(),
                node.getCordX(),
                node.getCordY(),
                node.getEventOnExecution(),
                node.getName(),
                node.getDisplayName()
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