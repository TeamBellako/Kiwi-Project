package com.kiwi.features.users.controllers;

import com.kiwi.features.users.data.AppUsageType;
import com.kiwi.features.users.data.UserAppUsagePersistence;
import com.kiwi.features.users.data.UsersPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAppUsageRepository extends JpaRepository<UserAppUsagePersistence, Long> {
    boolean existsByUserAndAppType(UsersPersistence user, AppUsageType appType);
    Optional<UserAppUsagePersistence> findByUserAndAppType(UsersPersistence user, AppUsageType appType);
}
