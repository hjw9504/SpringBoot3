package com.example.jdkproject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "tb_chat_room_member")
@IdClass(ChatRoomMemberId.class)
public class ChatRoomMemberVo {
    @Id
    private Long roomId;
    @Id
    private String memberId;
    private LocalDateTime registerTime;
}
