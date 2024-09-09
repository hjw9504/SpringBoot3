package com.example.jdkproject.client;

import com.example.jdkproject.domain.KakaoOAuthRequest;
import com.example.jdkproject.domain.KakaoOAuthResponse;
import com.example.jdkproject.domain.OAuth2Request;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "oAuth2Client", url = "http://localhost:8081")
public interface OAuth2Client {
    @PostMapping(value = "/oauth2/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    KakaoOAuthResponse getIdToken(@RequestHeader String authorization, OAuth2Request oAuth2Request);
}

