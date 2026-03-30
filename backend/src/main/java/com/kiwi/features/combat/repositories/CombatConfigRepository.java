package com.kiwi.features.combat.repositories;

import com.kiwi.features.combat.data.persistence.CombatConfigPersistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CombatConfigRepository extends JpaRepository<CombatConfigPersistence, Long> {

}