package com.example.test.controller;

import com.example.test.exception.CommonErrorException;
import com.example.test.exception.ErrorResponse;
import com.example.test.exception.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    public ResponseEntity<ErrorResponse> handleCommonErrorException(CommonErrorException ex){
        ErrorResponse response = new ErrorResponse(ex.getErrorStatus());
        return new ResponseEntity<>(response, ex.getErrorStatus().getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleDefaultHandlerException(Exception e){
        ErrorResponse response = new ErrorResponse(ErrorStatus.NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
