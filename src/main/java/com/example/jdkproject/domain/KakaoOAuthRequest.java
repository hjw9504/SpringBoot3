package com.example.jdkproject.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KakaoOAuthRequest {
    String grant_type;
    String client_id;
    String redirect_uri;
    String code;
    String client_secret;
}
