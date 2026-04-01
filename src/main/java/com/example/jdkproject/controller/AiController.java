package com.example.jdkproject.controller;

import com.example.jdkproject.domain.Response;
import com.example.jdkproject.dto.AiRequest;
import com.example.jdkproject.dto.JtiInfo;
import com.example.jdkproject.dto.MemberFollowDto;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.service.AiService;
import com.example.jdkproject.service.FollowService;
import com.example.jdkproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
@Validated
public class AiController {
    private final static int SUCCESS = 0;

    private final UserService userService;
    private final AiService aiService;

    @PostMapping(value = "/chat")
    public Response<String> getAiMessage(@RequestHeader String token, @RequestBody AiRequest request) {

        try {
            JtiInfo jtiInfo = userService.verifyToken(token);
            return new Response<>(aiService.getGeminiResponse(request.getMessage()), HttpStatus.OK, SUCCESS);
        } catch (CommonErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }
    }

}
