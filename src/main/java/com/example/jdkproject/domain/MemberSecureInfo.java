package com.example.jdkproject.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSecureInfo {
    private String userId;
    private String privateKey;
    private String publicKey;
}
