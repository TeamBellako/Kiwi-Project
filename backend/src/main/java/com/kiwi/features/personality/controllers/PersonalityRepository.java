package com.kiwi.features.personality.controllers;

import com.kiwi.features.personality.data.Personality;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface PersonalityRepository extends JpaRepository<Personality, String> {
    Optional<Personality> findByUserEmail(String email);
}
