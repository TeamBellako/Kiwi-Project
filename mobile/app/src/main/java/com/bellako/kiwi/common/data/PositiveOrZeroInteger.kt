package com.bellako.kiwi.common.data

@JvmInline
value class PositiveOrZeroInteger private constructor(val value: Int) {
    companion object {
        fun isValid(value: Int): Boolean {
            return value >= 0;
        }

        fun of(value: Int): Result<PositiveOrZeroInteger> {
            return if (isValid(value)) {
                Result.success(PositiveOrZeroInteger(value))
            } else {
                Result.failure(IllegalArgumentException("Invalid int value"))
            }
        }
    }
}