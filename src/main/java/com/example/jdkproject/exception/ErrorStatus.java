package com.example.jdkproject.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorStatus {
    NOT_FOUND(HttpStatus.NOT_FOUND, 101, "NOT FOUND"),
    ALREADY_EXIST(HttpStatus.BAD_REQUEST, 102, "ALREAY EXIST"),
    PARAMETER_NOT_FOUND(HttpStatus.BAD_REQUEST, 103, "PARAMETER NOT FOUND"),
    WRONG_USER_PASSWORD(HttpStatus.BAD_REQUEST, 105, "WRONG USER PASSWORD"),
    TOKEN_VERIFY_FAIL(HttpStatus.BAD_REQUEST, 201, "TOKEN VERIFY FAIL"),
    POSTING_REGISTER_FAIL(HttpStatus.BAD_REQUEST, 301, "POSTING REGISTER FAIL"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 501, "SERVER ERROR");

    private HttpStatus status;
    private int errorCode;
    private String message;
}
