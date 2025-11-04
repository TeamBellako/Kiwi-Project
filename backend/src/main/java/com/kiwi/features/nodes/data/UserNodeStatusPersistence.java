package com.kiwi.features.nodes.data;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
@Entity
@Table(name = "user_node_status")
public class UserNodeStatusPersistence {

    @EmbeddedId
    private UserNodeStatusKey id;

    @Enumerated(EnumType.STRING)
    private NodeStatus status;

}