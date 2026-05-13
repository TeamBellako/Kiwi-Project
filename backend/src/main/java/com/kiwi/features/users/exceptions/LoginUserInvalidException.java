package com.kiwi.features.users.exceptions;

public class LoginUserInvalidException extends RuntimeException {
    public LoginUserInvalidException() {
        super("Incorrect email or password");
    }
}
