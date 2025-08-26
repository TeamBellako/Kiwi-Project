package com.kiwi.features.settings.controllers;

import com.kiwi.features.settings.data.SettingsPersistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettingsRepository extends JpaRepository<SettingsPersistence, String> {
    Optional<SettingsPersistence> findByUserEmail(String email);
}
