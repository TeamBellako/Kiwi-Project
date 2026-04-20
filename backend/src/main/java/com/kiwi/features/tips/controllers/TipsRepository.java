package com.kiwi.features.tips.controllers;

import com.kiwi.features.tips.data.TipPersistence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipsRepository extends JpaRepository<TipPersistence, Long> { }
