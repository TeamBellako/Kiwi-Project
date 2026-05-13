package com.kiwi.features.quests.controllers;

import com.kiwi.features.quests.data.UserQuestStatusPersistence;
import com.kiwi.features.quests.data.UserQuestStatusKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserQuestStatusRepository extends JpaRepository<UserQuestStatusPersistence, UserQuestStatusKey> {

    List<UserQuestStatusPersistence> findByIdUserId(Long userId);

    Optional<UserQuestStatusPersistence> findByIdUserIdAndIdQuestId(Long userId, int questId);
}