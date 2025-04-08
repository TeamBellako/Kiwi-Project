package com.kiwi.repository;

import com.kiwi.entity.HelloDB;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelloRepository extends JpaRepository<HelloDB, Integer> {
}