package com.kiwi.features.nodes.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NodesDomain {

    private Long id;
    private NodeStatus status;
    private int icon;
    private int price;
    private float cordX;
    private float cordY;
    private Long eventOnExecution;
    private String name;
    private String displayName;
    private List<Long> connectedNodeIds;
    private int mapId;
    private boolean isFirstNodeOfMap;
}