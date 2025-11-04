package com.kiwi.features.nodes.data;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class NodesDTO {
    public int id;
    public int order;
    public String status;
    public Integer price;

}