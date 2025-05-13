package com.kiwi.users;

public class UsersConflictException extends RuntimeException {
    public UsersConflictException(String email) {
        super(String.format("User with email %s already exists", email));
    }
}
