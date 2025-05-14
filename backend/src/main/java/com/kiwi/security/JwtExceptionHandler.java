package com.kiwi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class JwtExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(JwtExceptionHandler.class);
    
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

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<String> handleGenericJwtException(JwtException ex) {
        logger.warn("Generic JWT exception: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Unauthorized: Invalid JWT token.");
    }
}