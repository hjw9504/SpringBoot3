package com.example.jdkproject.service.thirdparty;

import com.example.jdkproject.client.NaverAuthClient;
import com.example.jdkproject.client.NaverOidcClient;
import com.example.jdkproject.domain.OAuth2Request;
import com.example.jdkproject.domain.OAuth2Response;
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
public class NaverService implements ThirdService {

    private final NaverAuthClient naverAuthClient;
    private final NaverOidcClient naverOidcClient;

    @Value("${oauth.base.url}")
    private String BASE_URL;

    @Override
    public String getOAuthType() {
        return "naver";
    }

    @Override
    public String getOAuthUrl(OAuthVo oAuthVo) {
        String oauthUrl = "https://nid.naver.com/oauth2.0/authorize";

        oauthUrl = UriComponentsBuilder.fromHttpUrl(oauthUrl)
                .queryParam("response_type", "code")
                .queryParam("client_id", oAuthVo.getClientId())
                .queryParam("redirect_uri", BASE_URL + "/oauth/callback/"+oAuthVo.getIdpType())
                .queryParam("state", generateState())
                .build()
                .toUriString();

        return oauthUrl;
    }

    @Override
    public OAuth2Response getOAuthResponse(OAuthVo oAuthVo, String code, String state) {
        String clientId = oAuthVo.getClientId();
        String clientSecret = oAuthVo.getClientSecret();

        OAuth2Request request = OAuth2Request.builder()
                .grant_type("authorization_code")
                .client_id(clientId)
                .client_secret(clientSecret)
                .code(code)
                .build();

        return naverAuthClient.getIdToken(request);
    }

    @Override
    public String verifyIDPToken(String token) {
        try {
            return naverOidcClient.verifyIdToken("Bearer "+token).getResponse().getId();
        } catch (Exception e) {
            log.warn("Error verifying idp access token : {}", token);
            throw new CommonErrorException(ErrorStatus.TOKEN_VERIFY_FAIL);
        }
    }

    private static String generateState() {
        return RandomStringUtils.randomAlphabetic(20);
    }
}
