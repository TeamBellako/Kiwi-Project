package com.kiwi.features.nodes.controllers;

import com.kiwi.features.nodes.data.NodeStatus;
import com.kiwi.features.nodes.data.NodesDomain;
import org.springframework.stereotype.Service;

@Service
public class NodesProgressService {

    public NodesDomain lock(NodesDomain node) {
        if (node.getStatus() == NodeStatus.INACCESSIBLE) {
            return new NodesDomain(node.getId(),
                    NodeStatus.LOCKED,
                    node.getIcon(),
                    node.getPrice(),
                    node.getCordX(),
                    node.getCordY(),
                    node.getName(),
                    node.getDisplayName(),
                    node.getConnectedNodeIds(),
                    node.getMapId(),
                    node.isFirstNodeOfMap(),
                    node.getOnExecutionEvent(),
                    node.getOnExecutionEntityId());
            
        }
        return node;
    }

    public NodesDomain unlock(NodesDomain node) {
        if (node.getStatus() != NodeStatus.LOCKED) {
            throw new IllegalStateException("Only LOCKED nodes can be OPEN");
        }
        return new NodesDomain(node.getId(),
                NodeStatus.OPEN,
                node.getIcon(),
                node.getPrice(),
                node.getCordX(),
                node.getCordY(),
                node.getName(),
                node.getDisplayName(),
                node.getConnectedNodeIds(),
                node.getMapId(),
                node.isFirstNodeOfMap(),
                node.getOnExecutionEvent(),
                node.getOnExecutionEntityId());
    }

    public NodesDomain complete(NodesDomain node) {
        if (node.getStatus() != NodeStatus.OPEN) {
            throw new IllegalStateException("Only OPEN nodes can be COMPLETED");
        }
        return new NodesDomain(node.getId(),
                NodeStatus.COMPLETED,
                node.getIcon(),
                node.getPrice(),
                node.getCordX(),
                node.getCordY(),
                node.getName(),
                node.getDisplayName(),
                node.getConnectedNodeIds(),
                node.getMapId(),
                node.isFirstNodeOfMap(),
                node.getOnExecutionEvent(),
                node.getOnExecutionEntityId());
    }
}