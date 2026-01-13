package com.kiwi.features.skills.controllers;

import com.kiwi.features.skills.data.UserSkillStatusKey;
import com.kiwi.features.skills.data.UserSkillStatusPersistence;
import com.kiwi.features.skills.data.UserSkillView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserSkillStatusRepository
        extends JpaRepository<UserSkillStatusPersistence, UserSkillStatusKey> {

    // ============================================================================================
    // ALL USER SKILLS
    // ============================================================================================

    @Query("""
        SELECT
            s.id           AS skillId,
            s.name         AS name,
            s.description  AS description,
            s.quote        AS quote,
            s.icon         AS icon,

            s.cooldownType AS cooldownType,
            s.cooldownGoalId AS cooldownGoalId,
            s.cooldownTimeMinutes AS cooldownTimeMinutes,
            s.cooldownOtherDescription AS cooldownOtherDescription,
            s.levelupSkillId AS levelupSkillId,

            us.isCooldown  AS isCooldown,
            us.cooldownUntil AS cooldownUntil,
            us.deckSlot    AS deckSlot
        FROM UserSkillStatusPersistence us
        JOIN us.skill s
        WHERE us.id.userId = :userId
    """)
    List<UserSkillView> findAllSkillsForUser(Long userId);

    // ============================================================================================
    // EQUIPPED SKILLS
    // ============================================================================================

    @Query("""
        SELECT
            s.id           AS skillId,
            s.name         AS name,
            s.description  AS description,
            s.quote        AS quote,
            s.icon         AS icon,

            s.cooldownType AS cooldownType,
            s.cooldownGoalId AS cooldownGoalId,
            s.cooldownTimeMinutes AS cooldownTimeMinutes,
            s.cooldownOtherDescription AS cooldownOtherDescription,
            s.levelupSkillId AS levelupSkillId,

            us.isCooldown  AS isCooldown,
            us.cooldownUntil AS cooldownUntil,
            us.deckSlot    AS deckSlot
        FROM UserSkillStatusPersistence us
        JOIN us.skill s
        WHERE us.id.userId = :userId
          AND us.deckSlot <> 0
    """)
    List<UserSkillView> findEquippedSkillsForUser(Long userId);

    // ============================================================================================
    // SINGLE SKILL BY USER
    // ============================================================================================

    Optional<UserSkillStatusPersistence> findByIdUserIdAndIdSkillId(Long userId, Long skillId);
}
