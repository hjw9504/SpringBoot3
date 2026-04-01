package com.example.jdkproject.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
public class GeminiRequest {
    private List<Content> contents;

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Content {
        private String role; // "user" 또는 "model"
        private List<Part> parts;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Part {
        private String text;
    }
}
