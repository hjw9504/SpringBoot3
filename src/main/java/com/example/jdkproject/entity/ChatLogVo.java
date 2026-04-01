package com.example.jdkproject.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "tb_chat_log")
public class ChatLogVo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonProperty("room_id")
    private Long roomId;
    @JsonProperty("member_id")
    private String memberId;
    private String message;
    @JsonProperty("register_time")
    private LocalDateTime registerTime;
}
