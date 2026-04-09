package com.example.jdkproject.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
public class NaverOAuthResponse {
    private String resultcode;
    private String message;
    private Response response; // 실제 유저 정보가 담긴 객체

    @Getter
    @NoArgsConstructor
    @ToString
    public static class Response {
        private String id;
        private String name;
    }
}
