package com.example.jdkproject.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IDPLoginDto {
    private String idpToken;
    private String idToken;
    private String idpType;
}
