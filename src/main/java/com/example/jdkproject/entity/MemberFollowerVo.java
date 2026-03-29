package com.example.jdkproject.entity;

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
@Entity(name = "member_follower")
public class MemberFollowerVo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String memberId;
    private String followMemberId;
    private LocalDateTime registerTime;
    private LocalDateTime followedTime;

    public void updateFollowTime(LocalDateTime localDateTime) {
        this.followedTime = localDateTime;
    }
}
