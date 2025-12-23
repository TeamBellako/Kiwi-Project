package com.kiwi.features.quests.data;

import java.util.List;
import java.util.stream.Collectors;

public class QuestMapper {

    // --------------------------------------------------------------------------------------------
    // QUEST → DOMAIN
    // --------------------------------------------------------------------------------------------
    public static QuestDomain toDomain(
            QuestPersistence quest,
            UserQuestStatusPersistence status,
            List<SubquestDomain> subquests
    ) {
        List<SubquestDomain> orderedSubquests = subquests.stream()
                .sorted((a, b) -> Integer.compare(a.getOrder(), b.getOrder()))
                .collect(Collectors.toList());

        return new QuestDomain(
                quest.getId(),
                quest.getName(),
                quest.getDescription(),
                quest.getExperience(),
                quest.getIcon(),
                status != null ? status.getStatus() : null,
                orderedSubquests
        );
    }

    // --------------------------------------------------------------------------------------------
    // QUEST DOMAIN → PERSISTENCE
    // --------------------------------------------------------------------------------------------

    public static QuestPersistence toPersistence(QuestDomain domain) {
        QuestPersistence quest = new QuestPersistence();
        quest.setId(domain.getQuestId());
        return quest;
    }

    // --------------------------------------------------------------------------------------------
    // QUEST DOMAIN → DTO
    // --------------------------------------------------------------------------------------------
    public static QuestDTO toDTO(QuestDomain domain) {
        QuestDTO dto = new QuestDTO();
        dto.setQuestId(domain.getQuestId());
        dto.setName(domain.getName());
        dto.setDescription(domain.getDescription());
        dto.setExperience(domain.getExperience());
        dto.setIcon(domain.getIcon());
        dto.setStatus(domain.getStatus() != null ? domain.getStatus().name() : null);

        List<SubquestDTO> subquestDTOs = domain.getSubquests().stream()
                .map(SubquestMapper::toDTO)
                .collect(Collectors.toList());

        dto.setSubquests(subquestDTOs);
        return dto;
    }


    // --------------------------------------------------------------------------------------------
    // QUEST PERSISTENCE + STATUS → DTO
    // --------------------------------------------------------------------------------------------
    public static QuestDTO toDTO(QuestPersistence quest, UserQuestStatusPersistence status) {
        QuestDTO dto = new QuestDTO();
        dto.setQuestId(quest.getId());
        dto.setName(quest.getName());
        dto.setDescription(quest.getDescription());
        dto.setExperience(quest.getExperience());
        dto.setIcon(quest.getIcon());
        dto.setStatus(status != null ? status.getStatus().name() : null);
        dto.setSubquests(List.of());
        return dto;
    }


    // --------------------------------------------------------------------------------------------
    // QUEST DOMAIN → USER QUEST STATUS PERSISTENCE
    // --------------------------------------------------------------------------------------------
    public static UserQuestStatusPersistence toPersistence(int userId, QuestDomain domain) {
        UserQuestStatusPersistence persistence = new UserQuestStatusPersistence();
        persistence.setId(new UserQuestStatusKey(userId, domain.getQuestId()));
        persistence.setStatus(domain.getStatus());
        persistence.setQuest(toPersistence(domain));
        return persistence;
    }

}
