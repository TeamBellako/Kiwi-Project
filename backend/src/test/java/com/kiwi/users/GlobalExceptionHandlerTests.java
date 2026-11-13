package com.kiwi.users;

import com.kiwi.common.exceptions.GlobalExceptionHandler;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.Assert.assertEquals;

public class GlobalExceptionHandlerTests {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    public void unexpectedExceptionReturnsGenericInternalServerError() {
        var response = handler.handleUnexpectedException(new IllegalStateException("database details"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected server error occurred", response.getBody().get("error"));
    }

    @Test
    public void responseStatusExceptionKeepsItsHttpStatus() {
        var response = handler.handleUnexpectedException(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Request could not be processed", response.getBody().get("error"));
    }
}
