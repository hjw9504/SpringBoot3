package com.example.jdkproject.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {
    private String memberId;
    @NotBlank
    private String userId;
    @NotBlank
    private String userPw;
    @NotBlank
    private String name;
    @NotBlank
    private String email;
    private String phone;
    private String nickName;
    private String privateKey;
    private String publicKey;
}
