package com.kiwi.features.nodes.controllers;

import com.kiwi.features.nodes.data.NodeStatus;
import com.kiwi.features.nodes.data.NodesDomain;
import org.springframework.stereotype.Service;

@Service
public class NodesProgressService {

    public NodesDomain lock(NodesDomain node) {
        if (node.getStatus() != NodeStatus.INACCESSIBLE) {
            throw new IllegalStateException("Only INACCESSIBLE nodes can be LOCKED");
        }
        return new NodesDomain(node.getId(),
                node.getNodeOrder(),
                NodeStatus.LOCKED,
                node.getPrice(),
                node.getCordX(),
                node.getCordY(),
                node.getEventOnExecution(),
                node.getName(),
                node.getDisplayName());
    }

    public NodesDomain unlock(NodesDomain node) {
        if (node.getStatus() != NodeStatus.LOCKED) {
            throw new IllegalStateException("Only LOCKED nodes can be OPEN");
        }
        return new NodesDomain(node.getId(),
                node.getNodeOrder(),
                NodeStatus.OPEN,
                node.getPrice(),
                node.getCordX(),
                node.getCordY(),
                node.getEventOnExecution(),
                node.getName(),
                node.getDisplayName()
        );
    }

    public NodesDomain complete(NodesDomain node) {
        if (node.getStatus() != NodeStatus.OPEN) {
            throw new IllegalStateException("Only OPEN nodes can be COMPLETED");
        }
        return new NodesDomain(node.getId(),
                node.getNodeOrder(),
                NodeStatus.COMPLETED,
                node.getPrice(),
                node.getCordX(),
                node.getCordY(),
                node.getEventOnExecution(),
                node.getName(),
                node.getDisplayName());
    }
}