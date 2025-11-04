package com.kiwi.features.nodes.data;

public class NodesDomain {

    private final int id;
    private final int order;
    private final NodeStatus status;
    private final Integer price;

    public NodesDomain(int id, int order, NodeStatus status, Integer price) {
        this.id = id;
        this.order = order;
        this.status = status;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public int getOrder() {
        return order;
    }

    public NodeStatus getStatus() {
        return status;
    }

    public Integer getPrice() {
        return price;
    }
}