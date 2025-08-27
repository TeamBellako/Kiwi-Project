package com.kiwi.features.users.exceptions;

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
public class UsersExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(UsersExceptionHandler.class);

    @ExceptionHandler(UsersInvalidException.class)
    public ResponseEntity<Map<String, String>> handleUsersInvalid(UsersInvalidException ex) {
        logger.error("Invalid user: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createErrorResponseBody(ex.getMessage()));
    }
    
    @ExceptionHandler(UsersConflictException.class)
    public ResponseEntity<Map<String, String>> handleUsersConflict(UsersConflictException ex) {
        logger.error("Users conflict: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createErrorResponseBody("A user with that information already exists"));
    }

    @ExceptionHandler(UsersNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUsersNotFound(UsersNotFoundException ex) {
        logger.error("User not found: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createErrorResponseBody("User not found"));
    }
}