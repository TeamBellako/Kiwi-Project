package com.kiwi.features.nodes.controllers;

import com.kiwi.features.nodes.data.NodeStatus;
import com.kiwi.features.nodes.data.NodesDomain;
import org.springframework.stereotype.Service;

@Service
public class NodesProgressService {

    public NodesDomain lock(NodesDomain node) {
        if (node.getStatus() != NodeStatus.INACCESSIBLE) {
            throw new IllegalStateException("Solo nodos INACCESSIBLE pueden pasar a LOCKED");
        }
        return new NodesDomain(node.getId(), node.getOrder(), NodeStatus.LOCKED, node.getPrice());
    }

    public NodesDomain unlock(NodesDomain node) {
        if (node.getStatus() != NodeStatus.LOCKED) {
            throw new IllegalStateException("Solo nodos LOCKED pueden pasar a OPEN");
        }
        return new NodesDomain(node.getId(), node.getOrder(), NodeStatus.OPEN, node.getPrice());
    }

    public NodesDomain complete(NodesDomain node) {
        if (node.getStatus() != NodeStatus.OPEN) {
            throw new IllegalStateException("Solo nodos OPEN pueden pasar a COMPLETED");
        }
        return new NodesDomain(node.getId(), node.getOrder(), NodeStatus.COMPLETED, node.getPrice());
    }
}