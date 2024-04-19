package com.odilo.interview.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@SuppressWarnings({"unchecked","rawtypes"})
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String INVALID_PARAMETER = "Invalid parameter";
    private static final String INVALID_CREDENTIALS = "Invalid credentials.";
    private static final String SOME_ERROR_OCCURRED_WHILE_EXECUTING_THE_SERVICE =
            "Some error occurred while executing the service";
    private static final String THIS_METHOD_IS_NOT_SUPPORTED_BY_THE_SERVICE =
            "This method is not supported by the service";

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                               HttpHeaders headers, HttpStatusCode status,
                                                               WebRequest request) {

        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

        List<ApiError> apiErrorList = fieldErrors
                .stream()
                .map(error -> buildApiError(INVALID_PARAMETER, "Invalid input for field: " + error.getField()))
                .collect(Collectors.toList());

        log.error("[methodArgumentNotValid] - Validation errors: {}", apiErrorList);
        return new ResponseEntity<>(apiErrorList, status);
    }

    @Override
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException exception,
                                                                      HttpHeaders headers, HttpStatusCode status,
                                                                      WebRequest request) {

        ApiError error = buildApiError(THIS_METHOD_IS_NOT_SUPPORTED_BY_THE_SERVICE, exception.getMessage());

        log.error("[httpRequestMethodNotSupported] - Error: {}", error);
        return new ResponseEntity<>(error, status);
    }

    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
                                                               HttpStatusCode status, WebRequest request) {
        ApiError error = buildApiError("LocalDate is invalid", "LocalDate has bad format, the correct is YYYY-MM-DD");

        log.error("[handleHttpMessageNotReadable] - Error: {}", error);
        return new ResponseEntity<>(error, status);
    }


    @ExceptionHandler(ApiException.class)
    public final ResponseEntity<Object> handleExceptions(ApiException apiException) {
        ApiError error = buildApiError("Error while executing the request",
                apiException.getErrorDescription());

        log.error("[handleExceptions] - Error: {}", error);
        return new ResponseEntity(error, apiException.getStatus());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public final ResponseEntity<Object> handleUserBadCredentialsException(BadCredentialsException exception,
                                                                          WebRequest request) {

        ApiError error = buildApiError(INVALID_CREDENTIALS, exception.getMessage());

        log.error("[handleExceptions] - Error: {}", error);
        return new ResponseEntity(error, FORBIDDEN);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public final ResponseEntity<Object> handleExceptions(InternalAuthenticationServiceException exception) {
        log.error("[handleExceptions] - Error: {}", "Authentication Service Error");
        ApiException apiException = (ApiException) exception.getCause();
        return this.handleExceptions(apiException);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleExceptions(Exception exception) {
        ApiError error = buildApiError(SOME_ERROR_OCCURRED_WHILE_EXECUTING_THE_SERVICE, exception.getMessage());

        log.error("[handleExceptions] - Error: {}", error);
        return new ResponseEntity<>(error, INTERNAL_SERVER_ERROR);
    }

    private ApiError buildApiError(String description, String message) {
        return ApiError.builder()
                .errorDescription(description)
                .errorMessage(message)
                .build();
    }
}