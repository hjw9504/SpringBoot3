package com.example.jdkproject.service.thirdparty;

import com.example.jdkproject.client.GoogleAuthClient;
import com.example.jdkproject.domain.thirdparty.OAuth2Request;
import com.example.jdkproject.entity.OAuthVo;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoogleService implements ThirdService {

    private final GoogleAuthClient googleAuthClient;

    @Value("${oauth.base.url}")
    private String BASE_URL;

    @Override
    public String getOAuthType() {
        return "google";
    }

    @Override
    public String getOAuthUrl(OAuthVo oAuthVo) {
        String oauthUrl = "https://accounts.google.com/o/oauth2/v2/auth";

        oauthUrl = UriComponentsBuilder.fromHttpUrl(oauthUrl)
                .queryParam("response_type", "code")
                .queryParam("client_id", oAuthVo.getClientId())
                .queryParam("redirect_uri", BASE_URL + "/oauth/callback/"+oAuthVo.getIdpType())
                .queryParam("state", generateState())
                .queryParam("scope", "openid https://www.googleapis.com/auth/userinfo.profile")
                .queryParam("access_type", "offline")
                .build()
                .toUriString();

        return oauthUrl;
    }

    @Override
    public String getOAuthResponse(OAuthVo oAuthVo, String code, String state) {

        OAuth2Request request = OAuth2Request.builder()
                .grant_type("authorization_code")
                .client_id(oAuthVo.getClientId())
                .client_secret(oAuthVo.getClientSecret())
                .code(code)
                .redirect_uri(BASE_URL + "/oauth/callback/"+oAuthVo.getIdpType())
                .build();

        return googleAuthClient.getIdToken(request).getId_token();
    }

    @Override
    public String verifyIDPToken(String token) {
        try {
            return googleAuthClient.verifyIdToken(token).getSub();
        } catch (Exception e) {
            log.warn("Error verifying idp access token : {}", token);
            throw new CommonErrorException(ErrorStatus.TOKEN_VERIFY_FAIL);
        }
    }

    private static String generateState() {
        return RandomStringUtils.randomAlphabetic(20);
    }
}
