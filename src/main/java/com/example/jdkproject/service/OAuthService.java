package com.example.jdkproject.service;

import com.example.jdkproject.client.KakaoAuthClient;
import com.example.jdkproject.client.KakaoOidcClient;
import com.example.jdkproject.client.OAuth2Client;
import com.example.jdkproject.domain.*;
import com.example.jdkproject.dto.IDPLoginDto;
import com.example.jdkproject.dto.IdpUser;
import com.example.jdkproject.entity.OAuthVo;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.repository.OAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuthService {
    private final OAuthRepository oAuthRepository;
    private final KakaoAuthClient kakaoClient;
    private final KakaoOidcClient kakaoOidcClient;
    private final OAuth2Client oAuth2Client;

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

        String scope = getScope(oAuthVo.getIdpType());

        oauthUrl = UriComponentsBuilder.fromHttpUrl(oauthUrl)
                .queryParam("response_type", "code")
                .queryParam("client_id", oAuthVo.getClientId())
                .queryParam("redirect_uri", "http://localhost:8080/oauth/callback/"+oAuthVo.getIdpType())
                .queryParam("scope", scope)
                .toUriString();

        return oauthUrl;
    }

    public IDPLoginDto getIdToken(OAuthVo oAuthVo, String code) {
        switch(oAuthVo.getIdpType()) {
            case "kakao":
                KakaoOAuthResponse response = getKakaoTokenResponse(oAuthVo, code);
                IDPLoginDto tokenResponse = IDPLoginDto.builder().accessToken(response.getAccess_token())
                        .idToken(response.getId_token())
                        .idpType(oAuthVo.getIdpType())
                        .build();
                return tokenResponse;
            case "oauth2":
                KakaoOAuthResponse res = getOAuth2TokenResponse(oAuthVo, code);
                IDPLoginDto tokenRes = IDPLoginDto.builder().accessToken(res.getAccess_token())
                        .idToken(res.getId_token())
                        .idpType(oAuthVo.getIdpType())
                        .build();
                return tokenRes;
            default:
                return null;
        }
    }

    public IdpUser verifyIDPToken(String token, String idpType) {
        KakaoOIDCResponse response = kakaoOidcClient.getOidcResponse("Bearer "+token);
        log.info("Response: {}", response);

        IdpUser idpUser = IdpUser.builder().idpUserId(response.getSub())
                .idpType(idpType)
                .build();

        return idpUser;
    }

    private String getOAuthUrl(String idpType) {
        switch(idpType) {
            case "kakao" : return "https://kauth.kakao.com/oauth/authorize";
            case "oauth2" : return "http://localhost:8081/oauth2/authorize";
            case "google" : return "https://oauth.google.com/oauth/authorize";
            default : return null;
        }
    }

    private String getScope(String idpType) {
        switch(idpType) {
            case "kakao" : return "openId";
            case "oauth2" : return "profile openid";
            case "google" : return "basic";
            default : return "basic";
        }
    }

    private KakaoOAuthResponse getKakaoTokenResponse(OAuthVo oAuthVo, String code) {
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
        return response;
    }

    private KakaoOAuthResponse getOAuth2TokenResponse(OAuthVo oAuthVo, String code) {
        String clientId = oAuthVo.getClientId();
        String redirectUrl = "http://localhost:8080/oauth/callback/"+oAuthVo.getIdpType();

        OAuth2Request request = OAuth2Request.builder()
                .grant_type("authorization_code")
                .redirect_uri(redirectUrl)
                .code(code)
                .scope(getScope("oauth2"))
                .build();
//
//        String authorization = "Basic "+"c3ByaW5nLW9hdXRoMjpzcHJpbmctb2F1dGgyLXNlY3JldA==";
//        log.info("AAAAA:{}", "aaaaa");
//        log.info("AAAAA:{}", oAuth2Client.getIdToken(authorization, request));
//        KakaoOAuthResponse response = oAuth2Client.getIdToken(authorization, request);
        return null;
    }
}
