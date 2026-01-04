package com.kiwi.features.nodes.data;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
@Entity
@Table(name = "nodes")
public class NodesPersistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "node_order", nullable = false)
    private int nodeOrder;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "cord_x", nullable = false)
    private float cordX;

    @Column(name = "cord_y", nullable = false)
    private float cordY;

    @Column(name = "event_on_execution", nullable = false)
    private Long eventOnExecution;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "display_name")
    private String displayName;

    public String getDisplayName() {
        return displayName != null ? displayName : "";
    }
}