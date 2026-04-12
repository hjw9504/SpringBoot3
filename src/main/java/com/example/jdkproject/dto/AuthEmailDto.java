package com.example.jdkproject.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthEmailDto {
    private String email;
    private String emailVerifyToken;
    private String authCode;
}
