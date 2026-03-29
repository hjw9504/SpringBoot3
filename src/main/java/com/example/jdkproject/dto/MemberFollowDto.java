package com.example.jdkproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberFollowDto {
    @JsonProperty("member_id")
    private String memberId;
    @JsonProperty("follow_member_id")
    private String followMemberId;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("follow_user_id")
    private String followUserId;
    @JsonProperty("register_time")
    private LocalDateTime registerTime;
    @JsonProperty("followed_time")
    private LocalDateTime followedTime;
    @JsonProperty("profile_image")
    private String profileImage;
    @JsonProperty("follow_profile_image")
    private String followProfileImage;
}