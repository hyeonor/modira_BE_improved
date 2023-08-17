package com.example.modiraa.exception;

import lombok.Getter;

@Getter
public class NotFoundCustomException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String message;

    public NotFoundCustomException(ErrorCode errorCode, String message) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.message = message;
    }

}