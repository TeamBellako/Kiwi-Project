package com.kiwi.features.users.controllers;

import com.kiwi.features.users.data.UsersPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<UsersPersistence, Long> {
    Optional<UsersPersistence> findByEmail(String email);

    boolean existsByEmail(String email);

    void deleteByEmail(String email);

    @Modifying
    @Query("UPDATE UsersPersistence u SET u.languagePreference = :language WHERE u.id = :userId")
    void updateUserLanguage(@Param("userId") Long userId, @Param("language") String language);
}
