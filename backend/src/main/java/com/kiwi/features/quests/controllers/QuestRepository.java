package com.kiwi.features.quests.controllers;

import com.kiwi.features.quests.data.QuestPersistence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestRepository extends JpaRepository<QuestPersistence, Long> {
}
