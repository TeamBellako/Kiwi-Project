package com.kiwi.features.nodes.exceptions;

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
public class NodeExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(NodeExceptionHandler.class);

    @ExceptionHandler(NodeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNodeNotFound(NodeNotFoundException ex) {
        logger.error("Node not found: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createErrorResponseBody(ex.getMessage()));
    }

    @ExceptionHandler(NodeLockedException.class)
    public ResponseEntity<Map<String, String>> handleNodeLocked(NodeLockedException ex) {
        logger.error("Node locked: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.LOCKED) // 423
                .contentType(MediaType.APPLICATION_JSON)
                .body(createErrorResponseBody(ex.getMessage()));
    }
}