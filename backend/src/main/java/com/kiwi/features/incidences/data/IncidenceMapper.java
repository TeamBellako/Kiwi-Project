package com.kiwi.features.incidences.data;

public class IncidenceMapper {

    // From DTO to Domain
    public static IncidenceDomain toDomain(IncidenceDTO incidenceDTO) {
        if (incidenceDTO == null) {
            return null;
        }
        return new IncidenceDomain(incidenceDTO.getName());
    }

    // From Domain to DTO
    public static IncidenceDTO toDTO(IncidenceDomain incidenceDomain) {
        if (incidenceDomain == null) {
            return null;
        }
        return new IncidenceDTO(incidenceDomain.getName());
    }

    // From Persistence to Domain
    public static IncidenceDomain fromPersistence(IncidencePersistence incidencePersistence) {
        if (incidencePersistence == null) {
            return null;
        }
        return new IncidenceDomain(incidencePersistence.getName());
    }

    // From Domain to Persistence
    public static IncidencePersistence toPersistence(IncidenceDomain incidenceDomain) {
        if (incidenceDomain == null) {
            return null;
        }
        return IncidencePersistence.builder()
                .name(incidenceDomain.getName())
                .build();
    }
}