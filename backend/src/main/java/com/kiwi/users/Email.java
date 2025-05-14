package com.kiwi.users;

import java.util.regex.Pattern;

public record Email(String value) {
    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Email {
        if (!isValid(value)) throw new UsersInvalidException("Email format is not valid");
    }

    private boolean isValid(String value) {
        return EMAIL_REGEX.matcher(value).matches();
    }
}