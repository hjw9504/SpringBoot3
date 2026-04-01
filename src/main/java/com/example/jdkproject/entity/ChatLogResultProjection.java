package com.example.jdkproject.entity;

import java.time.LocalDateTime;

public interface ChatLogResultProjection {
    long getRoomId();
    String getMemberId();
    String getMessage();
    LocalDateTime getRegisterTime();
    String getUserId();
    String getSender();
}
