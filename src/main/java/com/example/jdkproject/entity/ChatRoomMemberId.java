package com.example.jdkproject.entity;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // 이게 있어야 JPA가 중복 체크를 정확히 합니다.
public class ChatRoomMemberId implements Serializable {
    private Long roomId;
    private String memberId;
}