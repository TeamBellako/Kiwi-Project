package com.kiwi.features.quests.data;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
@Entity
@Table(name = "quests")
public class QuestPersistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int experience;

    @Column(nullable = false)
    private int icon;

    @Column(name = "on_completed_action")
    private String onCompletedAction;

    @Column(name = "on_completed_entity")
    private String onCompletedEntity = "";

    @Column(name = "on_completed_entity_id")
    private int onCompletedEntityId = 0;

}