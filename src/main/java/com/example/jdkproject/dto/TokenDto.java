package com.example.jdkproject.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TokenDto {
    private String memberId;
    private String accessToken;
    private String refreshToken;
}
