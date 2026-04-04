package com.example.jdkproject.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PostingDto {
    private int id;
    private String title;
    private String body;
    private long likes;
    private String registerTime;
    private int commentCount;
    private MemberInfo member;

    @Getter
    @Builder
    public static class MemberInfo {
        private String memberId;
        private String name;
        private String profileImage;
        @Builder.Default
        private boolean isLikeTrue = false;
    }
}