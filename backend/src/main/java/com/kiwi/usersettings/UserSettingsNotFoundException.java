package com.kiwi.usersettings;

public class UserSettingsNotFoundException extends RuntimeException {
    public UserSettingsNotFoundException(String email) {
        super(String.format("UserSettings with email %s not found", email));
    }
}