package com.kiwi.features.nodes.data;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
@Entity
@Table(name = "nodes")
public class NodesPersistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "icon", nullable = false)
    private int icon;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "cord_x", nullable = false)
    private float cordX;

    @Column(name = "cord_y", nullable = false)
    private float cordY;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "map_id")
    private int mapId;

    @Column(name = "is_first_node_of_map")
    private boolean isFirstNodeOfMap;
    
    @Column(name = "on_execution_action")
    private String onExecutionAction;

    @Column(name = "on_execution_entity")
    private String onExecutionEntity = "";

    @Column(name = "on_execution_entity_id")
    private int onExecutionEntityId = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "transition_style", nullable = false)
    private NodeTransitionStyle transitionStyle = NodeTransitionStyle.VEIL;

    @OneToMany(mappedBy = "fromNode", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NodeEdgePersistence> outgoingEdges = new ArrayList<>();

    public String getDisplayName() {
        return displayName != null ? displayName : "";
    }

}