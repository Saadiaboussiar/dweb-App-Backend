package com.example.dweb_App.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException{
    private final String errorCode;
    private final String field;
    private final HttpStatus httpStatus;

    public BusinessException(String errorCode, String message, String field) {
        super(message);
        this.errorCode = errorCode;
        this.field = field;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }
    public BusinessException(String errorCode, String message, String field, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.field = field;
        this.httpStatus = httpStatus;
    }
}
