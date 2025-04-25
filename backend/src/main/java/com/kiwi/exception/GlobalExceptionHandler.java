package com.kiwi.exception;

import com.kiwi.usersettings.UserSettingsConflictException;
import com.kiwi.usersettings.UserSettingsInvalidException;
import com.kiwi.usersettings.UserSettingsNotFoundException;
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

    @ExceptionHandler(UserSettingsNotFoundException.class)
    public ResponseEntity<String> handleUserSettingsNotFound(UserSettingsNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UserSettingsInvalidException.class)
    public ResponseEntity<String> handleUserSettingsInvalid(UserSettingsInvalidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(UserSettingsConflictException.class)
    public ResponseEntity<String> handleUserSettingsConflict(UserSettingsConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errorMessages = new HashMap<>();
        
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errorMessages.put("message", error.getDefaultMessage());
        }

        return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
    }
}
