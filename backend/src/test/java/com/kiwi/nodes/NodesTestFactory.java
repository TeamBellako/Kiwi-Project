package com.kiwi.nodes;

import com.kiwi.features.nodes.data.NodesDTO;
import com.kiwi.features.nodes.data.NodeStatus;
import com.kiwi.features.nodes.data.NodesDomain;
import com.kiwi.features.nodes.data.NodesPersistence;
import com.kiwi.features.nodes.data.UserNodeStatusPersistence;
import com.kiwi.features.nodes.data.UserNodeStatusKey;

public class NodesTestFactory {

    public static NodesPersistence persistenceNode(int id, int order, int price) {
        NodesPersistence n = new NodesPersistence();
        n.setId(id);
        n.setOrder(order);
        n.setPrice(price);
        return n;
    }

    public static UserNodeStatusPersistence persistenceStatus(int userId, int nodeId, NodeStatus status) {
        UserNodeStatusKey key = new UserNodeStatusKey(userId, nodeId);
        UserNodeStatusPersistence s = new UserNodeStatusPersistence();
        s.setId(key);
        s.setStatus(status);
        return s;
    }

    public static NodesDomain domainNode(int id, int order, NodeStatus status, int price) {
        return new NodesDomain(id, order, status, price);
    }

    public static Object userIdDTO(int id) {
        return new Object() { public int userId = id; };
    }

    public static NodesDTO dtoNode(int id, int order, NodeStatus status, int price) {
        return new NodesDTO(id, order, status.name(), price);
    }

    // Helpers para tests
    public static UserNodeStatusPersistence inaccessibleStatus(int userId, int nodeId) {
        return persistenceStatus(userId, nodeId, NodeStatus.INACCESSIBLE);
    }

    public static UserNodeStatusPersistence lockedStatus(int userId, int nodeId) {
        return persistenceStatus(userId, nodeId, NodeStatus.LOCKED);
    }

    public static UserNodeStatusPersistence openStatus(int userId, int nodeId) {
        return persistenceStatus(userId, nodeId, NodeStatus.OPEN);
    }

    public static UserNodeStatusPersistence completeStatus(int userId, int nodeId) {
        return persistenceStatus(userId, nodeId, NodeStatus.COMPLETED);
    }
}