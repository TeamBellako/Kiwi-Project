package com.kiwi.features.settings;

public class SettingsNotFoundException extends RuntimeException {
    public SettingsNotFoundException(String email) {
        super(String.format("Settings with email %s not found", email));
    }
}