package com.kiwi.features.nodes.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NodesDomain {

    private Long id;
    private int nodeOrder;
    private NodeStatus status;
    private int price;
    private float cordX;
    private float cordY;
    private Long eventOnExecution;
    private String name;
    private String displayName;



}