package com.example.jdkproject.exception;

import lombok.Getter;

@Getter
public class CommonErrorException extends RuntimeException {
    private ErrorStatus errorStatus;

    public CommonErrorException(ErrorStatus errorStatus) {
        this.errorStatus = errorStatus;
    }
}
