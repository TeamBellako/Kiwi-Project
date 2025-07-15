package com.kiwi.features.personality;

import com.kiwi.features.settings.SettingsInvalidException;
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
public class PersonalityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(PersonalityExceptionHandler.class);

    @ExceptionHandler(PersonalityInvalidException.class)
    public ResponseEntity<Map<String, String>> handleSettingsInvalid(PersonalityInvalidException ex) {
        logger.error("PersonalityInvalidException: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createErrorResponseBody(ex.getMessage()));
    }

    @ExceptionHandler(PersonalityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePersonalityNotFound(PersonalityNotFoundException ex) {
        logger.error("PersonalityNotFoundException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createErrorResponseBody(ex.getMessage()));
    }
}
