package com.example.jdkproject.client;

import com.example.jdkproject.dto.GeminiRequest;
import com.example.jdkproject.dto.GeminiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "geminiClient", url = "https://generativelanguage.googleapis.com/v1beta")
public interface GeminiClient {

    @PostMapping(value = "/models/{model}:generateContent", consumes = "application/json")
    GeminiResponse generateContent(
            @PathVariable("model") String model,
            @RequestParam("key") String apiKey,
            @RequestBody GeminiRequest request
    );
}