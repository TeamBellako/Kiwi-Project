package com.kiwi.features.combat.data.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStatusResistanceRepository extends JpaRepository<UserStatusResistancePersistence, Long> {

    List<UserStatusResistancePersistence> findByUserId(Long userId);

}