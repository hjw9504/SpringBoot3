package com.example.jdkproject.controller;

import com.example.jdkproject.domain.Response;
import com.example.jdkproject.dto.AuthEmailDto;
import com.example.jdkproject.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/email")
@Validated
public class EmailController {
    private final static int SUCCESS = 0;

    private final EmailService emailService;

    @PostMapping(value = "/auth/code")
    public Response<AuthEmailDto> sendAuthEmail(@RequestBody AuthEmailDto authEmailDto) {
        String emailVerifyToken = emailService.getAuthEmail(authEmailDto.getEmail());

        authEmailDto = AuthEmailDto.builder()
                .email(authEmailDto.getEmail())
                .emailVerifyToken(emailVerifyToken)
                .build();

        return new Response<>(authEmailDto, HttpStatus.OK, SUCCESS);
    }

    @PostMapping(value = "/verify/code")
    public Response<Void> verifyAuthEmail(@RequestBody AuthEmailDto authEmailDto) {
        emailService.checkAuthCode(authEmailDto.getEmail(), authEmailDto.getEmailVerifyToken(), authEmailDto.getAuthCode());
        return new Response<>(HttpStatus.OK, SUCCESS);
    }

}
