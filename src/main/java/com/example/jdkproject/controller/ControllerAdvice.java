package com.example.jdkproject.controller;

import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.domain.Response;
import com.example.jdkproject.exception.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(RuntimeException.class)
    public Object runtimeException(Exception e) {
        return e.getMessage();
    }

    @ExceptionHandler(CommonErrorException.class)
    public ResponseEntity<Response> handleCommonErrorException(CommonErrorException ex) {
        Response response = new Response(ex.getErrorStatus());
        return new ResponseEntity<>(response, ex.getErrorStatus().getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleDefaultHandlerException(Exception e) {
        Response response = new Response(ErrorStatus.PARAMETER_NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Response> handleMissingRequestHeaderException(Exception e) {
        Response response = new Response(ErrorStatus.PARAMETER_NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
