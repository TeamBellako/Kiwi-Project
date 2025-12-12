package com.kiwi.features.goals.exceptions;

public class GoalUnauthorizedException extends RuntimeException {
    public GoalUnauthorizedException(String message) {
        super(message);
    }
}
