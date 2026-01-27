package com.kiwi.features.sprites.controllers;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kiwi.features.sprites.data.ExpressionPersistence;

public interface ExpressionsRepository extends JpaRepository<ExpressionPersistence, Long> {

}
