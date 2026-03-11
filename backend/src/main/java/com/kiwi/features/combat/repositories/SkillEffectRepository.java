package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.persistence.SkillEffectPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillEffectRepository extends JpaRepository<SkillEffectPersistence, Long> {

    List<SkillEffectPersistence> findBySkillId(Long skillId);

}