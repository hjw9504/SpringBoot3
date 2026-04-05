package com.example.jdkproject.controller;

import com.example.jdkproject.annotation.TokenCheck;
import com.example.jdkproject.domain.Response;
import com.example.jdkproject.dto.AiRequest;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.service.AiService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
@Validated
public class AiController {
    private final static int SUCCESS = 0;

    private final AiService aiService;

    @TokenCheck
    @PostMapping(value = "/chat")
    public Response<String> getAiMessage(HttpServletRequest request, @RequestBody AiRequest aiRequest) {

        try {
            return new Response<>(aiService.getGeminiResponse(aiRequest.getMessage()), HttpStatus.OK, SUCCESS);
        } catch (CommonErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }
    }

}
