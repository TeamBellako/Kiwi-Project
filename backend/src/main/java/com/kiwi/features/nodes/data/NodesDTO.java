package com.kiwi.features.nodes.data;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class NodesDTO {
    public Long id;
    public String status;
    public int icon;
    public int price;
    public float cordX;
    public float cordY;
    private Long eventOnExecution;
    private String name;
    private String displayName;
    private List<Long> connectedNodeIds;
}