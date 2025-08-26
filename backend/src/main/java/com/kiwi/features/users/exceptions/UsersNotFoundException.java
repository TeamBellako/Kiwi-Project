package com.kiwi.features.users.exceptions;

public class UsersNotFoundException extends RuntimeException {
    public UsersNotFoundException(String email) {
      super(String.format("User with email %s not found", email));
    }
}
