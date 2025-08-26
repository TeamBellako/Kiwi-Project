package com.kiwi.common.types;

import java.util.regex.Pattern;

public record Password(String value) {
    private static final Pattern PASSWORD_REGEX =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$");

    public Password {
        if (!isValid(value)) throw new IllegalArgumentException("Invalid password format");
    }

    private boolean isValid(String value) {
        return PASSWORD_REGEX.matcher(value).matches();
    }
}