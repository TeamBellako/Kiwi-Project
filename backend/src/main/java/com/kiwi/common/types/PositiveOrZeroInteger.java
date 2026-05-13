package com.kiwi.common.types;

public record PositiveOrZeroInteger(int value) {
    public PositiveOrZeroInteger {
        if (value < 0) {
            throw new IllegalArgumentException("Value must be >= 0");
        }
    }

    public PositiveOrZeroInteger add(PositiveOrZeroInteger other) {
        if (other == null) throw new NullPointerException("other must not be null");
        return new PositiveOrZeroInteger(
                Math.min(Integer.MAX_VALUE, value + other.value())
        );
    }

    public PositiveOrZeroInteger subtract(PositiveOrZeroInteger other) {
        if (other == null) throw new NullPointerException("other must not be null");
        return new PositiveOrZeroInteger(
                Math.max(0, value - other.value())
        );
    }
}
