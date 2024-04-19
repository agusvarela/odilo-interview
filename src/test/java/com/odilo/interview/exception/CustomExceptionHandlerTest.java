package com.odilo.interview.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class CustomExceptionHandlerTest {

    private CustomExceptionHandler customExceptionHandler;

    @BeforeEach
    void setUp() {
        customExceptionHandler = new CustomExceptionHandler();
    }

    @Test
    void handleHttpRequestMethodNotSupported_ReturnsErrorResponse() {
        HttpRequestMethodNotSupportedException exception = mock(HttpRequestMethodNotSupportedException.class);
        HttpHeaders headers = new HttpHeaders();
        WebRequest request = mock(WebRequest.class);

        ResponseEntity<Object> responseEntity = customExceptionHandler
                .handleHttpRequestMethodNotSupported(exception, headers, METHOD_NOT_ALLOWED, request);

        assertEquals(METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
    }

    @Test
    void handleExceptions_ApiException_ReturnsErrorResponse() {
        ApiException apiException = new ApiException("Test error", NOT_FOUND);

        ResponseEntity<Object> responseEntity = customExceptionHandler.handleExceptions(apiException);

        assertEquals(NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void handleUserBadCredentialsException_ReturnsForbiddenErrorResponse() {
        BadCredentialsException exception = new BadCredentialsException("Invalid credentials");

        ResponseEntity<Object> responseEntity = customExceptionHandler
                .handleUserBadCredentialsException(exception, null);

        assertEquals(FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    void handleExceptions_GeneralException_ReturnsInternalServerErrorResponse() {
        Exception exception = new Exception("Some error");

        ResponseEntity<Object> responseEntity = customExceptionHandler.handleExceptions(exception);

        assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}
