package com.example.jdkproject.service;

import com.example.jdkproject.client.KakaoClient;
import com.example.jdkproject.domain.KakaoOAuthRequest;
import com.example.jdkproject.domain.KakaoOAuthResponse;
import com.example.jdkproject.entity.OAuthVo;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.repository.OAuthRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class OAuthService {
    private final OAuthRepository oAuthRepository;
    private final KakaoClient kakaoClient;

    public OAuthService(OAuthRepository oAuthRepository, KakaoClient kakaoClient) {
        this.oAuthRepository = oAuthRepository;
        this.kakaoClient = kakaoClient;
    }

    public OAuthVo getIdpOAuth(String idpType) {
        OAuthVo oAuthVo = oAuthRepository.findIdpOAuth(idpType);
        return oAuthVo;
    }

    public String getOAuthUrl(OAuthVo oAuthVo) {
        // idp type에 따른 url
        String oauthUrl = getOAuthUrl(oAuthVo.getIdpType());
        if (oauthUrl == null) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        oauthUrl = UriComponentsBuilder.fromHttpUrl(oauthUrl)
                .queryParam("response_type", "code")
                .queryParam("client_id", oAuthVo.getClientId())
                .queryParam("redirect_uri", "http://localhost:8080/oauth/callback/"+oAuthVo.getIdpType())
                .toUriString();

        return oauthUrl;
    }

    public String getIdToken(OAuthVo oAuthVo, String code) {

        String clientId = oAuthVo.getClientId();
        String redirectUrl = "http://localhost:8080/oauth/callback/"+oAuthVo.getIdpType();

        KakaoOAuthRequest request = KakaoOAuthRequest.builder()
                .grant_type("authorization_code")
                .client_id(clientId)
                .redirect_uri(redirectUrl)
                .code(code)
                .client_secret(oAuthVo.getClientSecret())
                .build();

        KakaoOAuthResponse response = kakaoClient.getIdToken(request);

        return response.getId_token();
    }

    private String getOAuthUrl(String idpType) {
        switch(idpType) {
            case "kakao" : return "https://kauth.kakao.com/oauth/authorize";
            case "google" : return "https://oauth.google.com/oauth/authorize";
            default : return null;
        }
    }
}
