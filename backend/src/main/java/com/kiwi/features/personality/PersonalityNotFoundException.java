package com.kiwi.features.personality;

public class PersonalityNotFoundException extends RuntimeException {
    public PersonalityNotFoundException(String email) {
        super(String.format("Personality with email %s not found", email));
    }
}