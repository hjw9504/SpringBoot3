package com.example.jdkproject.client;

import com.example.jdkproject.domain.KakaoOIDCResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "kakaoOidcClient", url = "https://kapi.kakao.com")
public interface KakaoOidcClient {
    @GetMapping(value = "/v1/oidc/userinfo", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    KakaoOIDCResponse getOidcResponse(@RequestHeader("Authorization") String authorization);
}

