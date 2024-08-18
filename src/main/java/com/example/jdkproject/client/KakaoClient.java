package com.example.jdkproject.client;

import com.example.jdkproject.domain.KakaoOAuthRequest;
import com.example.jdkproject.domain.KakaoOAuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "kakaoClient", url = "https://kauth.kakao.com")
public interface KakaoClient {
    @PostMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    KakaoOAuthResponse getIdToken(@RequestBody KakaoOAuthRequest kakaoOAuthRequest);
}
