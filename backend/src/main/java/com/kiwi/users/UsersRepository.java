package com.kiwi.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<UsersPersistence, Long> {
    UsersPersistence findByEmail(String email);
    boolean existsByEmail(String email);
}
