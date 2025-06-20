package com.kiwi.features.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<UsersPersistence, Long> {
    Optional<UsersPersistence> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    void deleteByEmail(String email);
}
