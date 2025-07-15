package com.kiwi.features.personality;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface PersonalityRepository extends JpaRepository<Personality, String> {
    Optional<Personality> findByUserEmail(String email);
}
