package com.kiwi.settings;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettingsRepository extends JpaRepository<Settings, String> {
    Optional<Settings> findByEmail(String email);
    boolean existsByEmail(String email);
}
