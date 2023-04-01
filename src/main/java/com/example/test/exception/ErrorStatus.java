package com.example.test.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorStatus {
    NOT_FOUND(HttpStatus.NOT_FOUND, 101, "NOT FOUND"),
    TOKEN_VERIFY_FAIL(HttpStatus.BAD_REQUEST, 201, "TOKEN VERIFY FAIL"),
    POSTING_REGISTER_FAIL(HttpStatus.BAD_REQUEST, 301, "POSTING REGISTER FAIL");

    private HttpStatus status;
    private int errorCode;
    private String message;
}
