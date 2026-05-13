package com.kiwi.features.quests.data;

 import jakarta.persistence.*;
 import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
 @Entity
 @Table(name = "user_quest_status")
 public class UserQuestStatusPersistence {

     @EmbeddedId
     private UserQuestStatusKey id;

     @Enumerated(EnumType.STRING)
     @Column(nullable = false)
     private QuestStatus status;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "quest_id", insertable = false, updatable = false)
     private QuestPersistence quest;
 }
