package com.example.jdkproject.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IdpUser {
    private String idpUserId;
    private String idpType;
}
