package com.kiwi.features.quests.data;

import com.kiwi.features.quests.controllers.SubquestRepository;

import java.util.*;
import java.util.stream.Collectors;

public class SubquestMapper {

    public static SubquestDomain toDomain(SubquestPersistence sq, UserSubquestStatusPersistence userStatus) {
        SubquestStatus status = (userStatus != null) ? userStatus.getStatus() : null;
        return new SubquestDomain(
                sq.getId(),
                sq.getName(),
                sq.getExperience(),
                sq.getOrderIndex(),
                status
        );
    }

    public static SubquestDomain toDomain(SubquestPersistence sq) {
        return new SubquestDomain(
                sq.getId(),
                sq.getName(),
                sq.getExperience(),
                sq.getOrderIndex(),
                null
        );
    }

    // Lista: Persistence list + lista de user statuses -> lista de Domain
    // Empareja por subquest id. Si no hay status, status será null.
    public static List<SubquestDomain> toDomainList(
            List<SubquestPersistence> subquests,
            List<UserSubquestStatusPersistence> userStatuses
    ) {
        if (subquests == null) return Collections.emptyList();

        Map<Integer, UserSubquestStatusPersistence> statusBySubId = (userStatuses == null)
                ? Collections.emptyMap()
                : userStatuses.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        s -> s.getId().getSubquestId(),
                        s -> s,
                        (a, b) -> a
                ));

        return subquests.stream()
                .map(sq -> toDomain(sq, statusBySubId.get(sq.getId())))
                .sorted(Comparator.comparingInt(SubquestDomain::getOrder))
                .collect(Collectors.toList());
    }

    // Domain -> DTO
    public static SubquestDTO toDTO(SubquestDomain domain) {
        SubquestDTO dto = new SubquestDTO();
        dto.setSubquestId(domain.getSubquestId());
        dto.setName(domain.getName());
        dto.setExperience(domain.getExperience());
        dto.setOrder(domain.getOrder());
        dto.setStatus(domain.getStatus() != null ? domain.getStatus().name() : null);
        return dto;
    }

    public static SubquestDTO toDTO(SubquestPersistence sq, UserSubquestStatusPersistence userStatus) {
        return toDTO(toDomain(sq, userStatus));
    }

    // Domain -> UserSubquestStatusPersistence (guardar estado del usuario)
    public static UserSubquestStatusPersistence toPersistence(int userId, SubquestDomain domain, SubquestRepository subRepo) {
        UserSubquestStatusPersistence persistence = new UserSubquestStatusPersistence();
        persistence.setId(new UserSubquestStatusKey(userId, domain.getSubquestId()));

        persistence.setStatus(domain.getStatus());
        SubquestPersistence subquestPersistence = subRepo.findById(domain.getSubquestId())
                .orElseThrow(() -> new IllegalStateException("Subquest no encontrada: " + domain.getSubquestId()));
        persistence.setSubquest(subquestPersistence);
        return persistence;
    }

    // Persistence + desired status -> UserSubquestStatusPersistence (inicializar subquests)
    public static UserSubquestStatusPersistence toPersistence(int userId, SubquestPersistence sq, SubquestStatus status) {
        UserSubquestStatusPersistence persistence = new UserSubquestStatusPersistence();
        persistence.setId(new UserSubquestStatusKey(userId, sq.getId()));
        persistence.setStatus(status);
        persistence.setSubquest(sq);
        return persistence;
    }
}
