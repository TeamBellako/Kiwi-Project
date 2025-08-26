package com.kiwi.features.metrics.data;

import com.kiwi.common.types.PositiveOrZeroInteger;
import com.kiwi.features.users.data.UsersPersistence;

import java.time.LocalDate;

import static com.kiwi.common.utils.FormatUtils.formatDate;

public class MetricsDataMapper {

    public static MetricsDomain toDomain(MetricsDTO dto) {
        return (dto == null) ? null :
                new MetricsDomain(
                        LocalDate.parse(dto.getDate()),
                        new PositiveOrZeroInteger(dto.getMaxGoodTimeSeconds()),
                        new PositiveOrZeroInteger(dto.getCurrentGoodTimeSeconds()),
                        new PositiveOrZeroInteger(dto.getMaxBadTimeSeconds()),
                        new PositiveOrZeroInteger(dto.getCurrentBadTimeSeconds())
                );
    }

    public static MetricsDomain toDomain(MetricsPersistence persistence) {
        return (persistence == null) ? null :
                new MetricsDomain(
                        persistence.getDate(),
                        new PositiveOrZeroInteger(persistence.getMaxGoodTimeSeconds()),
                        new PositiveOrZeroInteger(persistence.getCurrentGoodTimeSeconds()),
                        new PositiveOrZeroInteger(persistence.getMaxBadTimeSeconds()),
                        new PositiveOrZeroInteger(persistence.getCurrentBadTimeSeconds())
                );
    }

    public static MetricsPersistence toPersistence(UsersPersistence usersPersistence, MetricsDomain domain) {
        return (domain == null) ? null :
                MetricsPersistence.builder()
                        .user(usersPersistence)
                        .date(domain.getDate())
                        .maxGoodTimeSeconds(domain.getMaxGoodTimeSeconds().value())
                        .currentGoodTimeSeconds(domain.getCurrentGoodTimeSeconds().value())
                        .maxBadTimeSeconds(domain.getMaxBadTimeSeconds().value())
                        .currentBadTimeSeconds(domain.getCurrentBadTimeSeconds().value())
                        .build();
    }

    public static MetricsPersistence toPersistence(UsersPersistence usersPersistence, MetricsDTO dto) {
        return toPersistence(usersPersistence, toDomain(dto));
    }

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

    public static MetricsDTO toDTO(MetricsPersistence persistence) {
        return toDTO(toDomain(persistence));
    }
}
