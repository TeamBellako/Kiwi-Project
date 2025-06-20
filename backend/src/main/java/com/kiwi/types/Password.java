package com.kiwi.types;

import java.util.regex.Pattern;

public record Password(String value) {
    private static final Pattern PASSWORD_REGEX =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    public Password {
        if (!isValid(value)) throw new IllegalArgumentException("Password format is not valid");
    }

    private boolean isValid(String value) {
        return PASSWORD_REGEX.matcher(value).matches();
    }
}