package com.example.jdkproject.domain;

import com.example.jdkproject.exception.ErrorStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Response<T> {
    private T resultData;
    private HttpStatus status;
    private int errorCode;
    private String message;

    public Response(T resultData, HttpStatus status, int errorCode) {
        this.status = status;
        this.resultData = resultData;
        this.errorCode = errorCode;
    }

    public Response(ErrorStatus errorStatus) {
        this.status = errorStatus.getStatus();
        this.errorCode = errorStatus.getErrorCode();
        this.message = errorStatus.getMessage();
    }
}
