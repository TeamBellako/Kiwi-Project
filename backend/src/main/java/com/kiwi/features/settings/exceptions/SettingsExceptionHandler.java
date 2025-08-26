package com.kiwi.features.settings.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import static com.kiwi.common.utils.HTTPUtils.createErrorResponseBody;

@RestControllerAdvice
public class SettingsExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(SettingsExceptionHandler.class);

    @ExceptionHandler(SettingsNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleSettingsNotFound(SettingsNotFoundException ex) {
        logger.error("Users settings not found: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createErrorResponseBody("Users settings not found"));
    }

    @ExceptionHandler(SettingsInvalidException.class)
    public ResponseEntity<Map<String, String>> handleSettingsInvalid(SettingsInvalidException ex) {
        logger.error("Invalid user settings: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createErrorResponseBody(ex.getMessage()));
    }

    @ExceptionHandler(SettingsConflictException.class)
    public ResponseEntity<Map<String, String>> handleSettingsConflict(SettingsConflictException ex) {
        logger.error("User settings conflict: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createErrorResponseBody("A user settings instance with that information already exists"));
    }
}
