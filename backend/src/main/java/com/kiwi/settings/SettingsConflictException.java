package com.kiwi.settings;

public class SettingsConflictException extends RuntimeException {
    public SettingsConflictException(Integer id) {
        super(String.format("Settings with id %d already exists", id));
    }
    public SettingsConflictException(String email) {
        super(String.format("Settings with email %s already exists", email));
    }
}