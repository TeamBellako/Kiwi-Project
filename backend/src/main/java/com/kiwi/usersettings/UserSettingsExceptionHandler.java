package com.kiwi.usersettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import static com.kiwi.utils.HTTPUtils.createErrorResponseBody;

@RestControllerAdvice
public class UserSettingsExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserSettingsExceptionHandler.class);

    @ExceptionHandler(UserSettingsNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserSettingsNotFound(UserSettingsNotFoundException ex) {
        logger.error("Users settings not found: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createErrorResponseBody("Users settings not found"));
    }

    @ExceptionHandler(UserSettingsInvalidException.class)
    public ResponseEntity<Map<String, String>> handleUserSettingsInvalid(UserSettingsInvalidException ex) {
        logger.error("Invalid user settings: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createErrorResponseBody(ex.getMessage()));
    }

    @ExceptionHandler(UserSettingsConflictException.class)
    public ResponseEntity<Map<String, String>> handleUserSettingsConflict(UserSettingsConflictException ex) {
        logger.error("User settings conflict: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createErrorResponseBody("A user settings instance with that information already exists"));
    }
}
