package com.kiwi.features.quests.controllers;

import com.kiwi.features.quests.data.SubquestPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubquestRepository extends JpaRepository<SubquestPersistence, Long> {

    List<SubquestPersistence> findAllByQuestIdOrderByOrderIndex(Long questId);

}
