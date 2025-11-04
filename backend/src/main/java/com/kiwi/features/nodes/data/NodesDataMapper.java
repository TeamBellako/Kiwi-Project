package com.kiwi.features.nodes.data;

public class NodesDataMapper {

    public static NodesDomain toDomain(NodesPersistence node, UserNodeStatusPersistence userStatus) {
        return NodesDomainFactory.create(node, userStatus);

    }

    public static NodesDTO toDTO(NodesDomain node) {
        return new NodesDTO(
                node.getId(),
                node.getOrder(),
                node.getStatus().name(),
                node.getPrice()
        );
    }

    public static NodesDTO toDTO(NodesPersistence node, UserNodeStatusPersistence status) {
        return new NodesDTO(
                node.getId(),
                node.getOrder(),
                status != null ? status.getStatus().name() : null,
                node.getPrice()
        );
    }

    public static UserNodeStatusPersistence toPersistence(int userId, NodesDomain node) {
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