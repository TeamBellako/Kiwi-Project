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

    @ExceptionHandler(CreateUserInvalidException.class)
    public ResponseEntity<Map<String, String>> handleCreateUserInvalid(CreateUserInvalidException ex) {
        logger.error("Create user invalid: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createErrorResponseBody(ex.getMessage()));
    }
    
    @ExceptionHandler(CreateUserConflictException.class)
    public ResponseEntity<Map<String, String>> handleCreateUserConflict(CreateUserConflictException ex) {
        logger.error("Create user conflict: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createErrorResponseBody(ex.getMessage()));
    }

    @ExceptionHandler(UsersNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UsersNotFoundException ex) {
        logger.error("User not found: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createErrorResponseBody(ex.getMessage()));
    }

    @ExceptionHandler(LoginUserInvalidException.class)
    public ResponseEntity<Map<String, String>> handleLoginUserInvalid(LoginUserInvalidException ex) {
        logger.error("Login user invalid: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createErrorResponseBody(ex.getMessage()));
    }
}