package com.kiwi.usersettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserSettingsExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserSettingsExceptionHandler.class);

    @ExceptionHandler(UserSettingsNotFoundException.class)
    public ResponseEntity<String> handleUserSettingsNotFound(UserSettingsNotFoundException ex) {
        logger.error("Users settings not found: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Users settings not found");
    }

    @ExceptionHandler(UserSettingsInvalidException.class)
    public ResponseEntity<String> handleUserSettingsInvalid(UserSettingsInvalidException ex) {
        logger.error("Invalid user settings: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UserSettingsConflictException.class)
    public ResponseEntity<String> handleUserSettingsConflict(UserSettingsConflictException ex) {
        logger.error("User settings conflict: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("User settings conflict");
    }
}
