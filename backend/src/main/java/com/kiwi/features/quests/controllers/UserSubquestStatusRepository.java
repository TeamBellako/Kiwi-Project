package com.kiwi.features.quests.controllers;

import com.kiwi.features.quests.data.UserSubquestStatusPersistence;
import com.kiwi.features.quests.data.UserSubquestStatusKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserSubquestStatusRepository extends JpaRepository<UserSubquestStatusPersistence, UserSubquestStatusKey> {

    Optional<UserSubquestStatusPersistence> findByIdUserIdAndIdSubquestId(long userId, int subquestId);

    @Query("""
        SELECT us
        FROM UserSubquestStatusPersistence us
        JOIN us.subquest s
        WHERE us.id.userId = :userId
          AND s.quest.id = :questId
        ORDER BY s.orderIndex
    """)
    List<UserSubquestStatusPersistence> findByUserIdAndQuestIdOrdered(
            @Param("userId") long userId,
            @Param("questId") long questId
    );
}