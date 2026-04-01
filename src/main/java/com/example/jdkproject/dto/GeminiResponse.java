package com.example.jdkproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GeminiResponse {
    private List<Candidate> candidates;

    @Getter
    public static class Candidate {
        private Content content;
    }

    @Getter
    public static class Content {
        private List<Part> parts;
    }

    @Getter
    public static class Part {
        private String text;
    }
}