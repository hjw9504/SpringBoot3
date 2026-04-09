package com.example.jdkproject.service.thirdparty;

import com.example.jdkproject.domain.KakaoOIDCResponse;
import com.example.jdkproject.domain.OAuth2Response;
import com.example.jdkproject.entity.OAuthVo;

public interface ThirdService {

    String getOAuthType();

    String getOAuthUrl(OAuthVo oAuthVo);

    OAuth2Response getOAuthResponse(OAuthVo oAuthVo, String code, String state);

    String verifyIDPToken(String token);
}
