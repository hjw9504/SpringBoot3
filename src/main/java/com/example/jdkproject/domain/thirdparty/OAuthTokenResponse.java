package com.example.jdkproject.domain.thirdparty;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
public class OAuthTokenResponse {
    // Kakao
    private String sub;
    private String nickname;

    // Naver
    private String resultcode;
    private String message;
    private Response response;

    @Getter
    @NoArgsConstructor
    @ToString
    public static class Response {
        private String id;
        private String name;
    }
}
