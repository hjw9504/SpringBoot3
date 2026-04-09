package com.example.jdkproject.client;

import com.example.jdkproject.domain.NaverOAuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "naverOidcClient", url = "https://openapi.naver.com")
public interface NaverOidcClient {
    @PostMapping(value = "/v1/nid/me", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    NaverOAuthResponse verifyIdToken(@RequestHeader(name = "Authorization") String authorization);
}

