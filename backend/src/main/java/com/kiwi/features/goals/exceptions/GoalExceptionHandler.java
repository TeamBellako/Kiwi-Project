package com.kiwi.features.goals.exceptions;

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
public class GoalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GoalExceptionHandler.class);

    @ExceptionHandler(GoalNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleGoalNotFound(GoalNotFoundException ex) {
        logger.error("Goal not found: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createErrorResponseBody(ex.getMessage()));
    }
}
