package com.kiwi.utils;


import java.util.regex.Pattern;

public class RegexUtils {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        return pattern.matcher(email).matches();
    }
}