package com.example.jdkproject.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LoginDto {
    private String memberId;
    @NotBlank
    private String userId;
    @NotBlank
    private String userPw;
    private String name;
    private String email;
    private String phone;
    private String nickName;
    private String privateKey;
    private String publicKey;
}
