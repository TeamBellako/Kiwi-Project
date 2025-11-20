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
        dto.setStatus(domain.getStatus() != null ? domain.getStatus().name() : null);

        List<SubquestDTO> subquestDTOs = domain.getSubquests().stream()
                .map(SubquestMapper::toDTO)
                .collect(Collectors.toList());

        dto.setSubquests(subquestDTOs);
        return dto;
    }


    // --------------------------------------------------------------------------------------------
    // QUEST PERSISTENCE + STATUS → DTO
    // (usado al dar una quest al usuario)
    // --------------------------------------------------------------------------------------------
    public static QuestDTO toDTO(QuestPersistence quest, UserQuestStatusPersistence status) {
        QuestDTO dto = new QuestDTO();
        dto.setQuestId(quest.getId());
        dto.setName(quest.getName());
        dto.setDescription(quest.getDescription());
        dto.setExperience(quest.getExperience());
        dto.setStatus(status != null ? status.getStatus().name() : null);
        dto.setSubquests(List.of()); // cuando se inicializa la quest no hace falta cargar subquests
        return dto;
    }


    // --------------------------------------------------------------------------------------------
    // QUEST DOMAIN → USER QUEST STATUS PERSISTENCE
    // usado en completeQuest
    // --------------------------------------------------------------------------------------------
    public static UserQuestStatusPersistence toPersistence(int userId, QuestDomain domain) {
        UserQuestStatusPersistence persistence = new UserQuestStatusPersistence();
        persistence.setId(new UserQuestStatusKey(userId, domain.getQuestId().intValue()));
        persistence.setStatus(domain.getStatus());
        return persistence;
    }


    // --------------------------------------------------------------------------------------------
    // QUEST PERSISTENCE + NEW STATUS → USER QUEST STATUS PERSISTENCE
    // usado al inicializar una quest
    // --------------------------------------------------------------------------------------------
    public static UserQuestStatusPersistence toPersistence(int userId, QuestPersistence quest, QuestStatus status) {
        UserQuestStatusPersistence persistence = new UserQuestStatusPersistence();
        persistence.setId(new UserQuestStatusKey(userId, quest.getId().intValue()));
        persistence.setStatus(status);
        return persistence;
    }

}
