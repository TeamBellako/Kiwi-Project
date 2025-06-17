package com.kiwi.metrics;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MetricsFactory {
    private static final Random RANDOM = new Random();

    public static MetricsDTO generateRandomValidMetricDTO() {
        return new MetricsDTO(getRandomDate(), getRandomSteps(), getRandomScreenTime());
    }

    public static MetricsDTO generateRandomInvalidMetricDTO() {
        return new MetricsDTO(getRandomDate(), -getRandomSteps(), getRandomScreenTime().multipliedBy(-1));
    }
    
    public static Set<MetricsDTO> generateRandomMetricsSet(int size, boolean getValidValues) {
        Set<MetricsDTO> metricsDTOSet = new HashSet<>();
        for (int i = 0; i < size; i++) {
            metricsDTOSet.add(getValidValues ? generateRandomValidMetricDTO() : generateRandomInvalidMetricDTO());
        }
        return metricsDTOSet;
    }
    
    private static LocalDate getRandomDate() {
        int startYear = 2025;
        int endYear = 2026;

        int year = RANDOM.nextInt(endYear - startYear + 1) + startYear;
        int month = RANDOM.nextInt(12) + 1;
        int dayOfMonth = RANDOM.nextInt(28) + 1;

        return LocalDate.of(year, month, dayOfMonth);
    }
    
    private static Integer getRandomSteps() {
        return RANDOM.nextInt(10001);
    }
    
    private static Duration getRandomScreenTime() {
        long minutes = RANDOM.nextInt(231) + 10;
        return Duration.ofMinutes(minutes);
    }
}

