package com.example.jdkproject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "member_channel")
public class MemberChannelVo {
    @Id
    @Column(name = "member_id")
    private String memberId;
    private String idpUserId;
    private String idpType;
    private LocalDateTime registerTime;
}
