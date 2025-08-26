package com.kiwi.features.metrics.data;

import com.kiwi.features.metrics.exceptions.MetricsInvalidException;
import com.kiwi.common.types.PositiveOrZeroInteger;
import com.kiwi.features.users.data.UsersPersistence;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static com.kiwi.common.utils.FormatUtils.formatDate;

public class MetricsDataMapper {
    // Persistence -> Domain
    public static MetricsDomain toDomain(MetricsPersistence persistence) {
        if (persistence == null) return null;

        return new MetricsDomain(
                persistence.getDate(),
                persistence.getMaxGoodTimeSeconds(),
                persistence.getCurrentGoodTimeSeconds(),
                persistence.getMaxBadTimeSeconds(),
                persistence.getCurrentBadTimeSeconds()
        );
    }

    // Domain -> Persistence
    public static MetricsPersistence toPersistence(UsersPersistence usersPersistence, MetricsDomain domain) {
        if (domain == null) return null;
        
        return new MetricsPersistence(
                usersPersistence,
                domain.getDate(),
                domain.getMaxGoodTimeSeconds(),
                domain.getCurrentGoodTimeSeconds(),
                domain.getMaxBadTimeSeconds(),
                domain.getCurrentBadTimeSeconds()
        );
    }

    // DTO -> Domain
    public static MetricsDomain toDomain(MetricsDTO dto) {
        if (dto == null) return null;
        
        MetricsDomain metricsDomain;
        try {
            metricsDomain = new MetricsDomain(
                    LocalDate.parse(dto.getDate()),
                    new PositiveOrZeroInteger(dto.getMaxGoodTimeSeconds()),
                    new PositiveOrZeroInteger(dto.getCurrentGoodTimeSeconds()),
                    new PositiveOrZeroInteger(dto.getMaxBadTimeSeconds()),
                    new PositiveOrZeroInteger(dto.getCurrentBadTimeSeconds())
            );
        } catch (Exception e) {
            throw new MetricsInvalidException(e.getMessage());
        }
        
        return metricsDomain;
    }

    // Domain -> DTO
    public static MetricsDTO toDTO(MetricsDomain domain) {
        if (domain == null) return null;
        return new MetricsDTO(
                formatDate(domain.getDate()),
                domain.getMaxGoodTimeSeconds().value(),
                domain.getCurrentGoodTimeSeconds().value(),
                domain.getMaxBadTimeSeconds().value(),
                domain.getCurrentBadTimeSeconds().value()
        );
    }

    public static Set<MetricsDomain> toDomainSetFromDtoSet(Set<MetricsDTO> dtoSet) {
        if (dtoSet == null) return null;

        Set<MetricsDomain> domainSet = new HashSet<>();
        for (MetricsDTO dto : dtoSet) {
            domainSet.add(toDomain(dto));
        }

        return domainSet;
    }

    public static Set<MetricsDomain> toDomainSetFromPersistenceSet(Set<MetricsPersistence> persistenceSet) {
        if (persistenceSet == null) return null;

        Set<MetricsDomain> domainSet = new HashSet<>();
        for (MetricsPersistence persistence : persistenceSet) {
            domainSet.add(toDomain(persistence));
        }

        return domainSet;
    }

    public static Set<MetricsPersistence> toPersistenceSet(UsersPersistence usersPersistence, Set<MetricsDomain> domainSet) {
        if (domainSet == null) return null;

        Set<MetricsPersistence> persistenceSet = new HashSet<>();
        for (MetricsDomain domain : domainSet) {
            persistenceSet.add(toPersistence(usersPersistence, domain));
        }

        return persistenceSet;
    }

    public static Set<MetricsDTO> toDTOSet(Set<MetricsDomain> domainSet) {
        if (domainSet == null) return null;

        Set<MetricsDTO> dtoSet = new HashSet<>();
        for (MetricsDomain domain : domainSet) {
            dtoSet.add(toDTO(domain));
        }

        return dtoSet;
    }
}
