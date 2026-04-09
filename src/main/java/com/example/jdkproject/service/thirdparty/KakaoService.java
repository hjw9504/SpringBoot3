package com.example.jdkproject.service.thirdparty;

import com.example.jdkproject.client.KakaoAuthClient;
import com.example.jdkproject.client.KakaoOidcClient;
import com.example.jdkproject.domain.KakaoOIDCResponse;
import com.example.jdkproject.domain.OAuth2Response;
import com.example.jdkproject.domain.OAuth2Request;
import com.example.jdkproject.entity.OAuthVo;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService implements ThirdService {

    private final KakaoAuthClient kakaoAuthClient;
    private final KakaoOidcClient kakaoOidcClient;

    @Value("${oauth.base.url}")
    private String BASE_URL;

    @Override
    public String getOAuthType() {
        return "kakao";
    }

    @Override
    public String getOAuthUrl(OAuthVo oAuthVo) {
        String oauthUrl = "https://kauth.kakao.com/oauth/authorize";

        oauthUrl = UriComponentsBuilder.fromHttpUrl(oauthUrl)
                .queryParam("response_type", "code")
                .queryParam("client_id", oAuthVo.getClientId())
                .queryParam("redirect_uri", BASE_URL + "/oauth/callback/"+oAuthVo.getIdpType())
                .queryParam("scope", "openId")
                .toUriString();

        return oauthUrl;
    }

    @Override
    public OAuth2Response getOAuthResponse(OAuthVo oAuthVo, String code, String state) {
        String clientId = oAuthVo.getClientId();
        String redirectUrl = BASE_URL + "/oauth/callback/"+oAuthVo.getIdpType();

        OAuth2Request request = OAuth2Request.builder()
                .grant_type("authorization_code")
                .client_id(clientId)
                .client_secret(oAuthVo.getClientSecret())
                .redirect_uri(redirectUrl)
                .code(code)
                .build();

        return kakaoAuthClient.getIdToken(request);
    }

    @Override
    public String verifyIDPToken(String token) {
        try {
            return kakaoOidcClient.getOidcResponse("Bearer "+token).getSub();
        } catch (Exception e) {
            log.warn("Error verifying idp access token : {}", token);
            throw new CommonErrorException(ErrorStatus.TOKEN_VERIFY_FAIL);
        }
    }
}
