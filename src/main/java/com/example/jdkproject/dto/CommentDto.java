package com.example.jdkproject.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CommentDto {
    private int postingId;
    private List<CommentItem> comments;

    @Getter
    @Builder

    public static class CommentItem {
        private String memberId;
        private String userId;
        private String comment;
        private String registerTime;
        private String profileImage;
    }
}