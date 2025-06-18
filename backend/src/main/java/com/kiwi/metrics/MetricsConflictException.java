package com.kiwi.metrics;

import com.kiwi.users.Email;

import java.time.LocalDate;

public class MetricsConflictException extends RuntimeException {
    public MetricsConflictException(Email email, LocalDate date) {  
        super(String.format("A metric with email %s and date %s already exists", email.value(), date.toString()));
    }
}
