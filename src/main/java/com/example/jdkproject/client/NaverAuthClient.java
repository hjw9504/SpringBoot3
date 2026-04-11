package com.example.jdkproject.client;

import com.example.jdkproject.domain.thirdparty.OAuth2Request;
import com.example.jdkproject.domain.thirdparty.OAuth2Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "naverAuthClient", url = "https://nid.naver.com")
public interface NaverAuthClient {
    @PostMapping(value = "/oauth2.0/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    OAuth2Response getIdToken(@SpringQueryMap OAuth2Request OAuthRequest);
}

