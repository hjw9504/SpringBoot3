package com.example.jdkproject.domain;

import lombok.Data;

@Data
public class OAuth2Response {
    String token_type;
    String access_token;
    String id_token;
    Integer expires_in;
    String refresh_token;
    Integer refresh_token_expires_in;
    String scope;
}
