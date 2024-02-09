package com.example.jdkproject.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostingDto {
    private String id;
    private String memberId;
    private String title;
    private String body;
    private String registerTime;
    private String modTime;
    private String token;
}
