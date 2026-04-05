package com.example.jdkproject.controller;

import com.example.jdkproject.annotation.TokenCheck;
import com.example.jdkproject.domain.Response;
import com.example.jdkproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/token")
public class TokenController {
    private final UserService userService;
    private static final int SUCCESS = 0;

    @TokenCheck
    @PostMapping(value = "/verify", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Response<String> verifyToken() {
        return new Response<>("success", HttpStatus.OK, SUCCESS);
    }

    @PostMapping(value = "/refresh", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Response<String> refreshToken() {
        return new Response<>("success", HttpStatus.OK, SUCCESS);
    }
}
