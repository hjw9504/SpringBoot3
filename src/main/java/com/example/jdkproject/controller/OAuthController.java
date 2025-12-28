package com.example.jdkproject.controller;

import com.example.jdkproject.domain.Response;
import com.example.jdkproject.dto.IDPLoginDto;
import com.example.jdkproject.dto.IdpUser;
import com.example.jdkproject.entity.OAuthVo;
import com.example.jdkproject.enums.ResponseStatus;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.service.OAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequestMapping("/oauth")
public class OAuthController {
    final OAuthService oAuthService;

    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

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
    public Response<IDPLoginDto> getIdpCallback(@PathVariable String idpType, @RequestParam String code) {
        log.info("idp: {}", idpType);

        OAuthVo oAuthVo = oAuthService.getIdpOAuth(idpType);
        if (oAuthVo == null) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        IDPLoginDto response = oAuthService.getIdToken(oAuthVo, code);

        return new Response<>(response, HttpStatus.OK, ResponseStatus.SUCCESS.getCode());
    }

    @PostMapping(value = "/token/verify/{idpType}")
    @ResponseBody
    public Response verifyAccessToken(@RequestBody IDPLoginDto dto, @PathVariable String idpType) {
        IdpUser user = oAuthService.verifyIDPToken(dto.getAccessToken(), idpType);
        return new Response(user, HttpStatus.OK, ResponseStatus.SUCCESS.getCode());
    }
}
