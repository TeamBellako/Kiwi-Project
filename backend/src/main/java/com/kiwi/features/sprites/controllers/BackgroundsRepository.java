package com.kiwi.features.sprites.controllers;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kiwi.features.sprites.data.BackgroundPersistence;

public interface BackgroundsRepository extends JpaRepository<BackgroundPersistence, Long> {

}
