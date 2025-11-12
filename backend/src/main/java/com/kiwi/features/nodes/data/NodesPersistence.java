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
    private int nodeOrder;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "cord_x", nullable = false)
    private int cord_x;

    @Column(name = "cord_y", nullable = false)
    private int cord_y;
}