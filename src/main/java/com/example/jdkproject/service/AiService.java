package com.example.jdkproject.service;

import com.example.jdkproject.client.GeminiClient;
import com.example.jdkproject.dto.GeminiRequest;
import com.example.jdkproject.dto.GeminiResponse;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AiService {

    private final GeminiClient geminiClient;

    @Value("${ai.api.key}")
    private String apiKey;

    private final String ANTHROPIC_VERSION = "2023-06-01";

    public String getGeminiResponse(String prompt) {
        // 1. Gemini 규격에 맞는 요청 객체 생성
        GeminiRequest request = GeminiRequest.builder()
                .contents(List.of(
                        GeminiRequest.Content.builder()
                                .role("user")
                                .parts(List.of(GeminiRequest.Part.builder().text(prompt).build()))
                                .build()
                ))
                .build();

        try {
            GeminiResponse response = geminiClient.generateContent("gemini-2.5-flash", apiKey, request);

            // 3. 람다를 활용해 텍스트 추출
            return Optional.ofNullable(response.getCandidates())
                    .filter(c -> !c.isEmpty())
                    .map(c -> c.getFirst().getContent().getParts().getFirst().getText())
                    .orElseThrow(() -> new CommonErrorException(ErrorStatus.SERVER_ERROR));

        } catch (FeignException e) {
            log.error("Gemini API Call Failed: {}", e.contentUTF8());
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }
    }
}
