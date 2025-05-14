package com.kiwi.users;

import com.kiwi.usersettings.UserSettingsInvalidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UsersExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(UsersExceptionHandler.class);

    @ExceptionHandler(UsersInvalidException.class)
    public ResponseEntity<String> handleUsersInvalid(UsersInvalidException ex) {
        logger.error("Invalid user: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
    
    @ExceptionHandler(UsersConflictException.class)
    public ResponseEntity<String> handleUsersConflict(UsersConflictException ex) {
        logger.error("Users conflict: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("A user with that information already exists");
    }

    @ExceptionHandler(UsersNotFoundException.class)
    public ResponseEntity<String> handleUsersNotFound(UsersNotFoundException ex) {
        logger.error("User not found: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("User not found");
    }
}