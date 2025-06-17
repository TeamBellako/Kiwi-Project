package com.kiwi.metrics;

import java.time.Duration;

public record PositiveDuration(Duration value) {
    public PositiveDuration {
        if (!isValid(value)) throw new IllegalArgumentException("Invalid positive duration value");
    }

    private boolean isValid(Duration value) { return !value.isNegative();}
}
