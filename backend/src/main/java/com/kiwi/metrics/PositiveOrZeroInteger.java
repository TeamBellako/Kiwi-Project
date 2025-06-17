package com.kiwi.metrics;

public record PositiveOrZeroInteger(Integer value) {
    public PositiveOrZeroInteger {
        if (!isValid(value)) throw new IllegalArgumentException("Invalid positive integer value");
    }

    private boolean isValid(Integer value) { return value > 0;}
}
