package com.example.jdkproject.controller;

import com.example.jdkproject.annotation.TokenCheck;
import com.example.jdkproject.domain.Member;
import com.example.jdkproject.domain.Response;
import com.example.jdkproject.dto.*;
import com.example.jdkproject.enums.ResponseStatus;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.service.KafkaProducer;
import com.example.jdkproject.service.OAuthService;
import com.example.jdkproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;
    private final OAuthService oAuthService;
    private final KafkaProducer kafkaProducer;

    private final static int SUCCESS = 0;

    @GetMapping(value = "/")
    public String mainPage() {
        log.info("Init");
        return "Hello";
    }

    @GetMapping(value = "/callback")
    public String callbackTest(String code) {
        log.info("Callback");
        return "Code: "+code;
    }

    @TokenCheck
    @GetMapping(value = "/user/info")
    public Response<List<Member>> getInfo(HttpServletRequest request,
                                          @RequestParam(required = false) String id) {
        log.info("User Info: {}", id);

        JtiInfo jtiInfo = (JtiInfo) request.getAttribute("jtiInfo");
        List<Member> result = userService.checkId(id, jtiInfo.getMemberId());
        if (result == null) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        return new Response<>(result, HttpStatus.OK, SUCCESS);
    }

    @PostMapping(value = "/user/register", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Response<String> registInfo(@Valid @RequestBody UserDto userDto) throws NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        userService.register(userDto);
        return new Response<>("success", HttpStatus.OK, SUCCESS);
    }

    @PostMapping(value = "/user/idp/register")
    public Response<Void> isIdpRegister(@RequestBody IDPLoginDto dto) {
        userService.registerIdp(dto);
        return new Response<>(HttpStatus.OK, ResponseStatus.SUCCESS.getCode());
    }

    @GetMapping(value = "/check/userId", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Response<Boolean> checkUserId(@Valid @RequestParam(value = "user_id") String userId) {
        Boolean result = userService.checkExistPlayer(userId);
        return new Response<>(result, HttpStatus.OK, SUCCESS);
    }

    @PostMapping(value = "/user/login", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Response<Member> getMemberInfo(@Valid @RequestBody LoginDto loginDto) throws NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        log.info("User Login");
        Member member = userService.login(loginDto.getUserId(), loginDto.getUserPw());

        //login message
//        kafkaProducer.sendMessage(member);

        return new Response<>(member, HttpStatus.OK, SUCCESS);
    }

    @PostMapping(value = "/user/idp/login", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Response<Member> getIdpMemberInfo(@Valid @RequestBody IDPLoginDto idpLoginDto) {

        IdpUser idpUser = oAuthService.verifyIDPToken(idpLoginDto.getIdpToken(), idpLoginDto.getIdpType());

        Member member = userService.loginIdp(idpUser.getIdpUserId(), idpUser.getIdpType());

        //login message
        // kafkaProducer.sendMessage(member);

        return new Response<>(member, HttpStatus.OK, SUCCESS);
    }

    @PostMapping(value = "/reset/password", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Response resetPassword(@RequestBody Member member) {
        if (member.getUserId() == null || member.getNewUserPw() == null) {
            throw new CommonErrorException(ErrorStatus.PARAMETER_NOT_FOUND);
        }

        userService.resetPassword(member.getUserId(), member.getNewUserPw());

        return new Response("success", HttpStatus.OK, SUCCESS);
    }

    @TokenCheck
    @PostMapping(value = "/update/nickname", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Response updateNickName(@RequestBody Member member) {
        userService.updateNickName(member);

        return new Response<>("success", HttpStatus.OK, SUCCESS);
    }
}
