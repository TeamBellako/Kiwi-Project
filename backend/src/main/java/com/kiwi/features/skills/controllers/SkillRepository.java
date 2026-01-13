package com.kiwi.features.skills.controllers;

import com.kiwi.features.skills.data.SkillPersistence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<SkillPersistence, Long> {
}
