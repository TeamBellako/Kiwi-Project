package com.kiwi.features.metrics;

import com.kiwi.types.Email;

import java.time.LocalDate;

public class MetricsNotFoundException extends RuntimeException {
    public MetricsNotFoundException(Email email, LocalDate date) {
        super(String.format("Metrics with email %s and date %s not found", email.value(), date.toString()));
    }
}
