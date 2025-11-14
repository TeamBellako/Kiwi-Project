package com.kiwi.features.nodes.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NodesDomain {

    private final int id;
    private final int nodeOrder;
    private final NodeStatus status;
    private final int price;
    private final float cordX;
    private final float cordY;

    public NodesDomain(int id, int nodeOrder, NodeStatus status, Integer price, float cordX, float cordY) {
        this.id = id;
        this.nodeOrder = nodeOrder;
        this.status = status;
        this.price = price;
        this.cordX = cordX;
        this.cordY = cordY;
    }

}