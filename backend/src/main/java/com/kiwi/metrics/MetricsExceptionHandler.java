package com.kiwi.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import static com.kiwi.common.HTTPUtils.createErrorResponseBody;

@RestControllerAdvice
public class MetricsExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(MetricsExceptionHandler.class);

    @ExceptionHandler(MetricsInvalidException.class)
    public ResponseEntity<Map<String, String>> handleMetricsInvalid(MetricsInvalidException ex) {
        logger.error("Invalid metrics: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createErrorResponseBody(ex.getMessage()));
    }

    @ExceptionHandler(MetricsConflictException.class)
    public ResponseEntity<Map<String, String>> handleMetricsConflict(MetricsConflictException ex) {
        logger.error("Conflict metrics: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createErrorResponseBody("A metric with that information already exists"));
    }
}