package com.example.jdkproject.entity;

import java.time.LocalDateTime;

public interface FollowerResultProjection {
    String getMemberId();
    String getFollowMemberId();
    LocalDateTime getRegisterTime();
    LocalDateTime getFollowedTime();
    String getUserId();
    String getFollowUserId();
    String getProfileImage();
    String getFollowProfileImage();
}
