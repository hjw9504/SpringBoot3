package com.example.jdkproject.domain;

import lombok.Data;

@Data
public class MemberSecureInfo {
    private String userId;
    private String privateKey;
    private String publicKey;
}
