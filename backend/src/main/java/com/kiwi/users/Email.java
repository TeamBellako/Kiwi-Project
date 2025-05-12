package com.kiwi.users;

import java.util.regex.Pattern;

public final class Email {
    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String value;

    public Email(String value) {
        if(!isValid(value)) throw new IllegalArgumentException("Email format is not valid");
        
        this.value = value;
    }
    
    private boolean isValid(String value) {
        return EMAIL_REGEX.matcher(value).matches();
    }
    
    public String getValue() {
        return value;
    }
}