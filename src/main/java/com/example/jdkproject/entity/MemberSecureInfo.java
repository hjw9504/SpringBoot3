package com.example.jdkproject.entity;

import lombok.Data;

@Data
public class MemberSecureInfo {
    private String userId;
    private String privateKey;
    private String publicKey;
}
