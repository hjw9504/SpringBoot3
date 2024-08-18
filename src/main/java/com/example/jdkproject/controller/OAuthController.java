package com.example.jdkproject.controller;

import com.example.jdkproject.entity.OAuthVo;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.service.OAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
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
    public String getIdpCallback(@PathVariable String idpType, @RequestParam String code) {
        log.info("idp: {}", code);

        OAuthVo oAuthVo = oAuthService.getIdpOAuth(idpType);
        if (oAuthVo == null) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        String idToken = oAuthService.getIdToken(oAuthVo, code);


    }
}
