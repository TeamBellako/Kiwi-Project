package com.kiwi.features.skills.controllers;

import com.kiwi.features.skills.data.persistence.SkillPersistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<SkillPersistence, Long> {

    List<SkillPersistence> findByIdIn(List<Long> ids);
}
