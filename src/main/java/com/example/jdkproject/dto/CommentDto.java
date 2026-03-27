package com.example.jdkproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentDto {
    private int idx;
    @JsonProperty(value = "posting_id")
    private int postingId;
    @JsonProperty(value = "member_id")
    private String memberId;
    private String comment;
    @JsonProperty(value = "register_time")
    private String registerTime;
    private MemberInfo member;

    @Getter
    @Builder
    public static class MemberInfo {
        private String memberId;
        private String name;
        private String profileImage;
    }
}