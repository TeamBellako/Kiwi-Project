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
    // QUEST DOMAIN → USER QUEST STATUS PERSISTENCE
    // --------------------------------------------------------------------------------------------
    public static UserQuestStatusPersistence toPersistence(Long userId, QuestStatus status,QuestPersistence questPersistence) {
        UserQuestStatusPersistence persistence = new UserQuestStatusPersistence();
        persistence.setId(new UserQuestStatusKey(userId, questPersistence.getId()));
        persistence.setStatus(status);
        persistence.setQuest(questPersistence);
        return persistence;
    }

}
