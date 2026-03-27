package com.example.jdkproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CommentDto {
    @JsonProperty("posting_id")
    private int postingId;
    private List<CommentItem> comments;

    @Getter
    @Builder
    public static class CommentItem {
        @JsonProperty("member_id")
        private String memberId;
        private String comment;
        @JsonProperty("register_time")
        private String registerTime;
    }
}