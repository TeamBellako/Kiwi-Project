package com.kiwi.features.nodes.data;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class NodesDTO {
    public Long id;
    public int nodeOrder;
    public String status;
    public int price;
    public float cordX;
    public float cordY;
    private Long eventOnExecution;
    private String name;
    private String displayName;

}