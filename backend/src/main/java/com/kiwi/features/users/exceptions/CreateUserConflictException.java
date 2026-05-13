package com.kiwi.features.users.exceptions;

public class CreateUserConflictException extends RuntimeException {
    public CreateUserConflictException(String email) {
        super(String.format("User with email %s already exists", email));
    }
}
