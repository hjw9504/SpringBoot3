package com.example.jdkproject.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ErrorResponse {
    private HttpStatus status;
    private int errorCode;
    private String message;

    public ErrorResponse(ErrorStatus errorStatus) {
        this.status = errorStatus.getStatus();
        this.errorCode = errorStatus.getErrorCode();
        this.message = errorStatus.getMessage();
    }
}
