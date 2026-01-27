package com.kiwi.features.sprites.controllers;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kiwi.features.sprites.data.FxPersistence;

public interface FxsRepository extends JpaRepository<FxPersistence, Long> {

}
