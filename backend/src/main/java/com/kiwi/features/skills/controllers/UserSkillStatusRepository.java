package com.kiwi.features.skills.controllers;

import com.kiwi.features.skills.data.UserSkillStatusKey;
import com.kiwi.features.skills.data.UserSkillStatusPersistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSkillStatusRepository
        extends JpaRepository<UserSkillStatusPersistence, UserSkillStatusKey> {

    List<UserSkillStatusPersistence> findByIdUserId(Long userId);

    Optional<UserSkillStatusPersistence> findByIdUserIdAndDeckSlot(
            Long userId,
            int deckSlot
    );

    Optional<UserSkillStatusPersistence> findByIdUserIdAndIdSkillId(Long userId, Long skillId);
}
