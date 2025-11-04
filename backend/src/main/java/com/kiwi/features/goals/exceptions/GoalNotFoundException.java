package com.kiwi.features.goals.exceptions;

public class GoalNotFoundException extends RuntimeException {
    public GoalNotFoundException(String id) {
        super(String.format("Goal with id %s not found", id));
    }
}
