package com.example.jdkproject.client;

import com.example.jdkproject.domain.thirdparty.OAuthTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "naverOidcClient", url = "https://openapi.naver.com")
public interface NaverOidcClient {
    @GetMapping(value = "/v1/nid/me")
    OAuthTokenResponse verifyIdToken(@RequestHeader("Authorization") String authorization);
}

