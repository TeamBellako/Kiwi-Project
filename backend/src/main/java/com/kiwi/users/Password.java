package com.kiwi.users;

import java.util.regex.Pattern;

public record Password(String value) {
    private static final Pattern PASSWORD_REGEX =
            Pattern.compile("^[A-Za-z0-9._%+-]$");

    public Password {
        if (!isValid(value)) throw new IllegalArgumentException("Password format is not valid");
    }

    private boolean isValid(String value) {
        return PASSWORD_REGEX.matcher(value).matches();
    }
}