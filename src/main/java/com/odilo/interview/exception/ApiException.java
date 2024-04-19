package com.odilo.interview.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApiException extends RuntimeException {

    private HttpStatus status;
    private String errorDescription;

    public ApiException(String errorDescription, HttpStatus httpStatus) {
        this.status = httpStatus;
        this.errorDescription = errorDescription;
    }

}
