package com.kiwi.features.incidences.data;

public class IncidenceMapper {

    // From DTO to Domain
    public static IncidenceDomain toDomain(IncidenceDTO incidenceDTO) {
        if (incidenceDTO == null) {
            return null;
        }
        return new IncidenceDomain(incidenceDTO.getName(), incidenceDTO.isValue());
    }

    // From Domain to DTO
    public static IncidenceDTO toDTO(IncidenceDomain incidenceDomain) {
        if (incidenceDomain == null) {
            return null;
        }
        return new IncidenceDTO(incidenceDomain.getName(), incidenceDomain.isValue());
    }

    // From Persistence to Domain
    public static IncidenceDomain fromPersistence(IncidencePersistance incidencePersistance) {
        if (incidencePersistance == null) {
            return null;
        }
        return new IncidenceDomain(incidencePersistance.getName(), incidencePersistance.isValue());
    }

    // From Domain to Persistence
    public static IncidencePersistance toPersistence(IncidenceDomain incidenceDomain) {
        if (incidenceDomain == null) {
            return null;
        }
        return IncidencePersistance.builder()
                .name(incidenceDomain.getName())
                .value(incidenceDomain.isValue())
                .build();
    }
}