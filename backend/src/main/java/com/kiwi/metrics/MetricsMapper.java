package com.kiwi.metrics;


import java.util.HashSet;
import java.util.Set;

public class MetricsMapper {
    // Persistence -> Domain
    public static Metrics toDomain(MetricsPersistence persistence) {
        if (persistence == null) return null;
        
        return new Metrics(
                persistence.getDate(),
                persistence.getSteps(),
                persistence.getScreenTime()
        );
    }

    // Domain -> Persistence
    public static MetricsPersistence toPersistence(Metrics domain) {
        if (domain == null) return null;
        return new MetricsPersistence(
                domain.getDate(),
                domain.getSteps(),
                domain.getScreenTime()
        );
    }

    // DTO -> Domain
    public static Metrics toDomain(MetricsDTO dto) {
        if (dto == null) return null;
        return new Metrics(
                dto.getDate(),
                new PositiveOrZeroInteger(dto.getSteps()),
                new PositiveDuration(dto.getScreenTime())
        );
    }

    // Domain -> DTO
    public static MetricsDTO toDTO(Metrics domain) {
        if (domain == null) return null;
        return new MetricsDTO(
                domain.getDate(),
                domain.getSteps().value(),
                domain.getScreenTime().value()
        );
    }

    public static Set<Metrics> toDomainSet(Set<MetricsDTO> dtoSet) {
        if (dtoSet == null) return null;
        
        Set<Metrics> domainSet = new HashSet<>();
        for (MetricsDTO dto : dtoSet) {
            domainSet.add(toDomain(dto)); 
        }
        
        return domainSet;
    }

    public static Set<MetricsDTO> toDTOSet(Set<Metrics> domainSet) {
        if (domainSet == null) return null;
        
        Set<MetricsDTO> dtoSet = new HashSet<>();
        for (Metrics domain : domainSet) {
            dtoSet.add(toDTO(domain));
        }
        
        return dtoSet;
    }
}
