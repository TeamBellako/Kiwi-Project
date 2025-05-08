package com.kiwi.usersettings;

public class UserSettingsNotFoundException extends RuntimeException {
    public UserSettingsNotFoundException(Integer id) {
        super(String.format("UserSettings with id %d not found", id));
    }

    public UserSettingsNotFoundException(String email) {
        super(String.format("UserSettings with email %s not found", email));
    }
}