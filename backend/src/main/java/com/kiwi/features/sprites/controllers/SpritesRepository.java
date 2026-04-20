package com.kiwi.features.sprites.controllers;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kiwi.features.sprites.data.SpritePersistence;

public interface SpritesRepository extends JpaRepository<SpritePersistence, Long> {

}
