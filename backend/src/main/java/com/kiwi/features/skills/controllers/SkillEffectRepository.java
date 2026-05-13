package com.kiwi.features.skills.controllers;

import com.kiwi.features.skills.data.persistence.SkillEffectPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillEffectRepository extends JpaRepository<SkillEffectPersistence, Long> {

    List<SkillEffectPersistence> findBySkillIdIn(List<Long> skillIds);

}