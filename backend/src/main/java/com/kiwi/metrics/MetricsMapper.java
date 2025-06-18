package com.kiwi.metrics;

import com.kiwi.users.Email;
import com.kiwi.users.UsersPersistence;
import com.kiwi.users.UsersRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.Optional;

public class MetricsMapper {
    // Persistence -> Domain
    public static Metrics toDomain(MetricsPersistence persistence) {
        if (persistence == null) return null;

        return new Metrics(
                persistence.getUser().getEmail(),
                persistence.getDate(),
                persistence.getSteps(),
                persistence.getScreenTime()
        );
    }

    // Domain -> Persistence
    public static MetricsPersistence toPersistence(UsersPersistence usersPersistence, Metrics domain) {
        if (domain == null) return null;
        
        return new MetricsPersistence(
                usersPersistence,
                domain.getDate(),
                domain.getSteps(),
                domain.getScreenTime()
        );
    }

    // DTO -> Domain
    public static Metrics toDomain(MetricsDTO dto) {
        if (dto == null) return null;
        return new Metrics(
                new Email(dto.getEmail()),
                dto.getDate(),
                new PositiveOrZeroInteger(dto.getSteps()),
                new PositiveDuration(dto.getScreenTime())
        );
    }

    // Domain -> DTO
    public static MetricsDTO toDTO(Metrics domain) {
        if (domain == null) return null;
        return new MetricsDTO(
                domain.getEmail().value(),
                domain.getDate(),
                domain.getSteps().value(),
                domain.getScreenTime().value()
        );
    }

    public static Set<Metrics> toDomainSetFromDtoSet(Set<MetricsDTO> dtoSet) {
        if (dtoSet == null) return null;

        Set<Metrics> domainSet = new HashSet<>();
        for (MetricsDTO dto : dtoSet) {
            domainSet.add(toDomain(dto));
        }

        return domainSet;
    }

    public static Set<Metrics> toDomainSetFromPersistenceSet(Set<MetricsPersistence> persistenceSet) {
        if (persistenceSet == null) return null;

        Set<Metrics> domainSet = new HashSet<>();
        for (MetricsPersistence persistence : persistenceSet) {
            domainSet.add(toDomain(persistence));
        }

        return domainSet;
    }

    public static Set<MetricsPersistence> toPersistenceSet(UsersPersistence usersPersistence, Set<Metrics> domainSet) {
        if (domainSet == null) return null;

        Set<MetricsPersistence> persistenceSet = new HashSet<>();
        for (Metrics domain : domainSet) {
            persistenceSet.add(toPersistence(usersPersistence, domain));
        }

        return persistenceSet;
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
