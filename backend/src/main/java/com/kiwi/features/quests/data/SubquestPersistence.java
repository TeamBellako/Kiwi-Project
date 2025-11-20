package com.kiwi.features.quests.data;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
@Entity
@Table(name = "subquests")
public class SubquestPersistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id", nullable = false)
    private QuestPersistence quest;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int experience;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;
}