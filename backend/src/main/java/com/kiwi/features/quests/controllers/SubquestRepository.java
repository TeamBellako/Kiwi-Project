package com.kiwi.features.quests.controllers;

import com.kiwi.features.quests.data.SubquestPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubquestRepository extends JpaRepository<SubquestPersistence, Integer> {

    List<SubquestPersistence> findAllByQuestIdOrderByOrderIndex(int questId);

}
