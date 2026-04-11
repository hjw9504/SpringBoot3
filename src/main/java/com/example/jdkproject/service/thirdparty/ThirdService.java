package com.example.jdkproject.service.thirdparty;

import com.example.jdkproject.entity.OAuthVo;

public interface ThirdService {

    String getOAuthType();

    String getOAuthUrl(OAuthVo oAuthVo);

    String getOAuthResponse(OAuthVo oAuthVo, String code, String state);

    String verifyIDPToken(String token);
}
