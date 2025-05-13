package com.kiwi.exception;

import com.kiwi.users.UsersConflictException;
import com.kiwi.usersettings.UserSettingsConflictException;
import com.kiwi.usersettings.UserSettingsInvalidException;
import com.kiwi.usersettings.UserSettingsNotFoundException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserSettingsNotFoundException.class)
    public ResponseEntity<String> handleUserSettingsNotFound(UserSettingsNotFoundException ex) {
        logger.error("UsersPersistence settings not found: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
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
                .body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        logger.warn("Validation failed: {}", ex.getMessage());
        
        Map<String, String> errorMessages = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errorMessages.put(error.getField(), error.getDefaultMessage());
        }

        return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<String> handleSignatureException(SignatureException ex) {
        logger.warn("Invalid JWT signature: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Unauthorized: Invalid or tampered JWT signature.");
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<String> handleMalformedJwtException(MalformedJwtException ex) {
        logger.warn("Malformed JWT: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Unauthorized: Malformed JWT token.");
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String> handleExpiredJwtException(ExpiredJwtException ex) {
        logger.warn("Expired JWT: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Unauthorized: Token has expired. Please log in again.");
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<String> handleUnsupportedJwtException(UnsupportedJwtException ex) {
        logger.warn("Unsupported JWT: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Unauthorized: Unsupported JWT token.");
    }

    @ExceptionHandler(PrematureJwtException.class)
    public ResponseEntity<String> handlePrematureJwtException(PrematureJwtException ex) {
        logger.warn("Premature JWT: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Unauthorized: JWT token is not valid yet.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Illegal argument in JWT processing: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Bad Request: JWT token is missing or malformed.");
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<String> handleGenericJwtException(JwtException ex) {
        logger.warn("Generic JWT exception: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Unauthorized: Invalid JWT token.");
    }
    
    @ExceptionHandler(UsersConflictException.class)
    public ResponseEntity<String> handleUsersConflict(UsersConflictException ex) {
        logger.error("Users conflict: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }
}
