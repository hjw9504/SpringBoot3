package com.example.jdkproject.service;

import com.example.jdkproject.client.KakaoAuthClient;
import com.example.jdkproject.client.KakaoOidcClient;
import com.example.jdkproject.domain.*;
import com.example.jdkproject.dto.IDPLoginDto;
import com.example.jdkproject.dto.IdpUser;
import com.example.jdkproject.entity.MemberChannelVo;
import com.example.jdkproject.entity.OAuthVo;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.repository.MemberChannelRepository;
import com.example.jdkproject.repository.OAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuthService {
    private final OAuthRepository oAuthRepository;
    private final MemberChannelRepository memberChannelRepository;
    private final KakaoAuthClient kakaoClient;
    private final KakaoOidcClient kakaoOidcClient;

    @Value("${oauth.base.url}")
    private String BASE_URL;

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
                .queryParam("redirect_uri", BASE_URL + "/oauth/callback/"+oAuthVo.getIdpType())
                .queryParam("scope", getScope(oAuthVo.getIdpType()))
                .toUriString();

        return oauthUrl;
    }

    public IDPLoginDto getIdToken(OAuthVo oAuthVo, String code) {
        switch(oAuthVo.getIdpType()) {
            case "kakao":
                KakaoOAuthResponse response = getKakaoTokenResponse(oAuthVo, code);
                IDPLoginDto tokenResponse = IDPLoginDto.builder()
                        .idpToken(response.getAccess_token())
                        .idToken(response.getId_token())
                        .idpType(oAuthVo.getIdpType())
                        .build();

                return tokenResponse;
            case "oauth2":
                KakaoOAuthResponse res = getOAuth2TokenResponse(oAuthVo, code);
                IDPLoginDto tokenRes = IDPLoginDto.builder()
                        .idpToken(res.getAccess_token())
                        .idToken(res.getId_token())
                        .idpType(oAuthVo.getIdpType())
                        .build();
                return null;
            default:
                return null;
        }
    }

    public IdpUser verifyIDPToken(String token, String idpType) {
        try {
            KakaoOIDCResponse response = kakaoOidcClient.getOidcResponse("Bearer "+token);
            log.info("Response: {}", response);

            IdpUser idpUser = IdpUser.builder().idpUserId(response.getSub())
                    .idpType(idpType)
                    .build();

            return idpUser;
        } catch (Exception e) {
            log.warn("Error verifying idp access token : {}", token);
            throw new CommonErrorException(ErrorStatus.TOKEN_VERIFY_FAIL);
        }
    }

    public Optional<MemberChannelVo> getIdpRegisterResult(IDPLoginDto dto) {
        if ("kakao".equals(dto.getIdpType())) {
            IdpUser idpUser = verifyIDPToken(dto.getIdpToken(), dto.getIdpType());
            return memberChannelRepository.findUserByIdpUserIdAndIdpType(idpUser.getIdpUserId(), idpUser.getIdpType());
        }
        throw new CommonErrorException(ErrorStatus.TOKEN_VERIFY_FAIL);
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
        String redirectUrl = BASE_URL + "/oauth/callback/"+oAuthVo.getIdpType();

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
        String redirectUrl = BASE_URL + "/oauth/callback/"+oAuthVo.getIdpType();

        OAuth2Request request = OAuth2Request.builder()
                .grant_type("authorization_code")
                .redirect_uri(redirectUrl)
                .code(code)
                .scope(getScope("oauth2"))
                .build();

        return null;
    }
}
