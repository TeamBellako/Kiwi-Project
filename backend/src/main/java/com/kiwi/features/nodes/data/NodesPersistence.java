package com.kiwi.features.nodes.data;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
@Entity
@Table(name = "nodes")
public class NodesPersistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "node_order", nullable = false)
    private int order;

    @Column(name = "price")
    private Integer price;
}