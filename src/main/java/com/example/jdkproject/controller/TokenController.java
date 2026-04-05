package com.example.jdkproject.controller;

import com.example.jdkproject.annotation.TokenCheck;
import com.example.jdkproject.domain.Member;
import com.example.jdkproject.domain.Response;
import com.example.jdkproject.dto.TokenDto;
import com.example.jdkproject.service.JwtTokenService;
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
    private final JwtTokenService jwtTokenService;
    private static final int SUCCESS = 0;

    @TokenCheck
    @PostMapping(value = "/verify", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Response<String> verifyToken() {
        return new Response<>("success", HttpStatus.OK, SUCCESS);
    }

    @PostMapping(value = "/refresh", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Response<TokenDto> refreshToken(@RequestHeader(name = "X-Refresh-Token") String refreshToken, @RequestBody Member member) {
        TokenDto tokenDto = jwtTokenService.validateRefreshToken(refreshToken, member.getMemberId());
        return new Response<>(tokenDto, HttpStatus.OK, SUCCESS);
    }
}
