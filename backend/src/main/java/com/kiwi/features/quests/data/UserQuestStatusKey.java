package com.kiwi.features.quests.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode
@Embeddable
public class UserQuestStatusKey implements Serializable {

    @Column(name = "user_id")
    private long userId;

    @Column(name = "quest_id")
    private long questId;
}