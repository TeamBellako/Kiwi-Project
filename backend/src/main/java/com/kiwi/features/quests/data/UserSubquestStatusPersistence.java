package com.kiwi.features.quests.data;

import jakarta.persistence.*;
        import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
@Entity
@Table(name = "user_subquest_status")
public class UserSubquestStatusPersistence {

    @EmbeddedId
    private UserSubquestStatusKey id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubquestStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subquest_id", insertable = false, updatable = false)
    private SubquestPersistence subquest;
}
