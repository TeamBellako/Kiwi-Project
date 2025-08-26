package com.kiwi.metrics;

import com.kiwi.features.metrics.data.MetricsDTO;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static com.kiwi.common.utils.FormatUtils.formatDate;

public class MetricsFactory {
    private static final Random RANDOM = new Random();

    public static MetricsDTO generateRandomValidMetricDTO() {
        return new MetricsDTO(
            getRandomDate(),
            0,
            getRandomTimeSeconds(1, 2),
            0,
            getRandomTimeSeconds(3, 4)
        );
    }

    public static MetricsDTO generateRandomInvalidMetricDTO() {
        return new MetricsDTO(
            getRandomDate(),
            0,
            -getRandomTimeSeconds(1, 2),
            0,
            -getRandomTimeSeconds(3, 4)
        );
    }
    
    public static Set<MetricsDTO> generateRandomMetricsSet(int size, boolean getValidValues) {
        Set<MetricsDTO> metricsDTOSet = new HashSet<>();
        for (int i = 0; i < size; i++) {
            metricsDTOSet.add(getValidValues ? generateRandomValidMetricDTO() : generateRandomInvalidMetricDTO());
        }
        return metricsDTOSet;
    }
    
    private static String getRandomDate() {
        int startYear = 2025;
        int endYear = 2026;

        int year = RANDOM.nextInt(endYear - startYear + 1) + startYear;
        int month = RANDOM.nextInt(12) + 1;
        int dayOfMonth = RANDOM.nextInt(28) + 1;

        return formatDate(LocalDate.of(year, month, dayOfMonth));
    }
    
    private static Integer getRandomTimeSeconds(Integer fromHours, Integer untilHours) {
        return RANDOM.nextInt((untilHours - fromHours) * 60 * 60) + fromHours;
    }
}

