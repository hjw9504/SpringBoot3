package com.example.jdkproject.service;

import com.example.jdkproject.domain.*;
import com.example.jdkproject.dto.IDPLoginDto;
import com.example.jdkproject.dto.IdpUser;
import com.example.jdkproject.entity.MemberChannelVo;
import com.example.jdkproject.entity.OAuthVo;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.repository.MemberChannelRepository;
import com.example.jdkproject.repository.OAuthRepository;
import com.example.jdkproject.service.thirdparty.ThirdService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuthService {
    private final OAuthRepository oAuthRepository;
    private final MemberChannelRepository memberChannelRepository;

    private final List<ThirdService> thirdServices;
    private final Map<String, ThirdService> serviceMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (ThirdService service : thirdServices) {
            serviceMap.put(service.getOAuthType(), service);
        }
    }

    public String getOAuthUrl(String idpType) {
        OAuthVo oAuthVo = getIdpOAuth(idpType);
        return serviceMap.get(idpType).getOAuthUrl(oAuthVo);
    }

    public IDPLoginDto getIdToken(String idpType, String code, String state) {
        OAuthVo oAuthVo = getIdpOAuth(idpType);
        OAuth2Response response = serviceMap.get(idpType).getOAuthResponse(oAuthVo, code, state);

        return IDPLoginDto.builder()
                .idpToken(response.getAccess_token())
                .idToken(response.getId_token())
                .idpType(oAuthVo.getIdpType())
                .build();
    }

    public IdpUser verifyIDPToken(String token, String idpType) {
        String sub = serviceMap.get(idpType).verifyIDPToken(token);

        return IdpUser.builder()
                .idpUserId(sub)
                .idpType(idpType)
                .build();
    }

    public Optional<MemberChannelVo> getIdpRegisterResult(IDPLoginDto dto) {
        IdpUser idpUser = verifyIDPToken(dto.getIdpToken(), dto.getIdpType());
        return memberChannelRepository.findUserByIdpUserIdAndIdpType(idpUser.getIdpUserId(), idpUser.getIdpType());
    }

    private OAuthVo getIdpOAuth(String idpType) {
        return Optional.ofNullable(oAuthRepository.findIdpOAuth(idpType))
                .orElseThrow(() -> new CommonErrorException(ErrorStatus.NOT_FOUND));
    }
}
