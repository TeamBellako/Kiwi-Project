package com.kiwi.nodes;

import com.kiwi.features.nodes.data.NodesDTO;
import com.kiwi.features.nodes.data.NodeStatus;
import com.kiwi.features.nodes.data.NodesDomain;
import com.kiwi.features.nodes.data.NodesPersistence;
import com.kiwi.features.nodes.data.UserNodeStatusPersistence;
import com.kiwi.features.nodes.data.UserNodeStatusKey;

public class NodesTestFactory {

    public static NodesPersistence persistenceNode(int id, int nodeOrder, int price, float cordX, float cordY) {
        NodesPersistence n = new NodesPersistence();
        n.setId(id);
        n.setNodeOrder(nodeOrder);
        n.setPrice(price);
        n.setCordX(cordX);
        n.setCordY(cordY);
        return n;
    }

    public static UserNodeStatusPersistence persistenceStatus(Long userId, int nodeId, NodeStatus status) {
        UserNodeStatusKey key = new UserNodeStatusKey(userId, nodeId);
        UserNodeStatusPersistence s = new UserNodeStatusPersistence();
        s.setId(key);
        s.setStatus(status);
        return s;
    }

    public static NodesDomain domainNode(int id, int nodeOrder, NodeStatus status, int price, float cordX, float cordY) {
        return new NodesDomain(id, nodeOrder, status, price, cordX, cordY);
    }

    public static Object userIdDTO(Long id) {
        return new Object() { public Long userId = id; };
    }

    public static NodesDTO dtoNode(int id, int nodeOrder, NodeStatus status, int price, float cordX, float cordY) {
        return new NodesDTO(id, nodeOrder, status.name(), price, cordX, cordY);
    }

    // Helpers para tests
    public static UserNodeStatusPersistence inaccessibleStatus(Long userId, int nodeId) {
        return persistenceStatus(userId, nodeId, NodeStatus.INACCESSIBLE);
    }

    public static UserNodeStatusPersistence lockedStatus(Long userId, int nodeId) {
        return persistenceStatus(userId, nodeId, NodeStatus.LOCKED);
    }

    public static UserNodeStatusPersistence openStatus(Long userId, int nodeId) {
        return persistenceStatus(userId, nodeId, NodeStatus.OPEN);
    }

    public static UserNodeStatusPersistence completeStatus(Long userId, int nodeId) {
        return persistenceStatus(userId, nodeId, NodeStatus.COMPLETED);
    }
}