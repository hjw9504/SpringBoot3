package com.example.jdkproject.entity;

import java.time.LocalDateTime;

public interface CommentResultProjection {
    String getComment();
    String getMemberId();
    LocalDateTime getRegisterTime();
    String getUserId();
}
