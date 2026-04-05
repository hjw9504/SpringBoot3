package com.example.jdkproject.controller;

import com.example.jdkproject.domain.Response;
import com.example.jdkproject.dto.IDPLoginDto;
import com.example.jdkproject.dto.IdpUser;
import com.example.jdkproject.entity.OAuthVo;
import com.example.jdkproject.enums.ResponseStatus;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.service.OAuthService;
import com.example.jdkproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/oauth")
public class OAuthController {
    private final OAuthService oAuthService;
    private final UserService userService;

    @GetMapping(value = "/{idpType}")
    public RedirectView getIdpOAuthView(@PathVariable String idpType) {
        log.info("idp: {}", idpType);

        OAuthVo oAuthVo = oAuthService.getIdpOAuth(idpType);
        if (oAuthVo == null) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        String oAuthUrl = oAuthService.getOAuthUrl(oAuthVo);

        return new RedirectView(oAuthUrl);
    }

    @GetMapping(value = "/callback/{idpType}")
    public RedirectView getIdpCallback(@PathVariable String idpType, @RequestParam String code) {
        try {
            OAuthVo oAuthVo = oAuthService.getIdpOAuth(idpType);
            if (oAuthVo == null) {
                throw new CommonErrorException(ErrorStatus.NOT_FOUND);
            }

            IDPLoginDto response = oAuthService.getIdToken(oAuthVo, code);

            // register 결과 가져오기
            return new RedirectView("http://54.180.225.237/idp/result?idp_token="+response.getIdpToken()+"&idp_type="+response.getIdpType());
        } catch (CommonErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }
    }

    @PostMapping(value = "/token/verify/{idpType}")
    @ResponseBody
    public Response<IdpUser> verifyAccessToken(@RequestBody IDPLoginDto dto, @PathVariable String idpType) {
        IdpUser user = oAuthService.verifyIDPToken(dto.getIdpToken(), idpType);
        return new Response<>(user, HttpStatus.OK, ResponseStatus.SUCCESS.getCode());
    }

    @PostMapping(value = "/check/idp/register")
    public Response<Boolean> isIdpRegister(@RequestBody IDPLoginDto dto) {
        return new Response<>(oAuthService.getIdpRegisterResult(dto).isPresent(), HttpStatus.OK, ResponseStatus.SUCCESS.getCode());
    }
}
