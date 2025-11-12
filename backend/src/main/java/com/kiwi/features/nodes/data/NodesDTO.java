package com.kiwi.features.nodes.data;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class NodesDTO {
    public int id;
    public int nodeorder;
    public String status;
    public int price;
    public float cord_x;
    public float cord_y;

}