package com.kiwi.features.nodes.data;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode
@Embeddable
public class UserNodeStatusKey implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "node_id")
    private Long nodeId;
}