package com.kiwi.usersettings;

public class UserSettingsConflictException extends RuntimeException {
    public UserSettingsConflictException(Integer id) {
        super(String.format("UserSettings with id %d already exists", id));
    }
    public UserSettingsConflictException(String email) {
        super(String.format("UserSettings with email %s already exists", email));
    }
}