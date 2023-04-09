package com.example.jdkproject.domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostingRequest {
    private long postingId;
    private String memberId;
    private String token;
    private String title;
    private String contents;
    private String views;
    private String registerTime;
    private String imageUrl;
    private MultipartFile file;
    private String likes;
}
