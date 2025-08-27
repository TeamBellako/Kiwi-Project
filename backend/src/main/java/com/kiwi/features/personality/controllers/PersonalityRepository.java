package com.kiwi.features.personality.controllers;

import com.kiwi.features.personality.data.PersonalityPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface PersonalityRepository extends JpaRepository<PersonalityPersistence, String> {
    Optional<PersonalityPersistence> findByUserEmail(String email);
}
