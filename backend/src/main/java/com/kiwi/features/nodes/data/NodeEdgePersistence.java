package com.kiwi.features.nodes.data;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "node_edges")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class NodeEdgePersistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_node_id", nullable = false)
    private NodesPersistence fromNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_node_id", nullable = false)
    private NodesPersistence toNode;

    //FUTURE CONDITIONAL EDGES
   // type          (opcional: NORMAL(DEFAULT), QUEST_OPTIONAL, SUBQUEST_OPTIONAL, etc.)
   // condition     (opcional: id)
}