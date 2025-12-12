package com.kiwi.features.quests.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
        import java.io.Serializable;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode
@Embeddable
public class UserSubquestStatusKey implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "subquest_id")
    private int subquestId;
}