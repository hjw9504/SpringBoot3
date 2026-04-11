package com.example.jdkproject.client;

import com.example.jdkproject.domain.thirdparty.OAuth2Request;
import com.example.jdkproject.domain.thirdparty.OAuth2Response;
import com.example.jdkproject.domain.thirdparty.OAuthTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "googleAuthClient", url = "https://oauth2.googleapis.com")
public interface GoogleAuthClient {
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    OAuth2Response getIdToken(OAuth2Request OAuthRequest);

    @GetMapping(value = "/tokeninfo")
    OAuthTokenResponse verifyIdToken(@RequestParam("id_token") String idToken);
}

