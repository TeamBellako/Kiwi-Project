package com.kiwi.features.skills.controllers;

import com.kiwi.features.skills.data.persistence.EnemySkillKey;
import com.kiwi.features.skills.data.persistence.EnemySkillPersistence;
import com.kiwi.features.skills.data.persistence.SkillPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EnemySkillRepository extends JpaRepository<EnemySkillPersistence, EnemySkillKey> {

    @Query("""
            SELECT s
            FROM SkillPersistence s
            JOIN EnemySkillPersistence es
            ON s.id = es.id.skillId
            WHERE es.id.enemyId = :enemyId
    """)
    List<SkillPersistence> findSkillsByEnemyId(Long enemyId);

}