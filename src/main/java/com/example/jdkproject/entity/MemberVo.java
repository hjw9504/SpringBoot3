package com.example.jdkproject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "member")
public class MemberVo {
    @Id
    @Column(name = "member_id")
    private String memberId;
    private String userId;
    private String userPw;
    private String name;
    private String email;
    private String phone;
    private String nickname;
    private LocalDateTime registerTime;
    @Column(name = "recent_login_time")
    private LocalDateTime recentLoginTime;
    private String role;
    private String profileImage;
    private LocalDateTime updateNicknameTime;

    public MemberVo(String memberId) {
        this.memberId = memberId;
    }

    @Builder
    public MemberVo(String memberId, String userId, String userPw, String name, String email, String phone, String nickname, LocalDateTime registerTime, LocalDateTime recentLoginTime, String role, String profileImage, LocalDateTime updateNicknameTime) {
        this.memberId = memberId;
        this.userId = userId;
        this.userPw = userPw;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.nickname = nickname;
        this.registerTime = registerTime;
        this.recentLoginTime = recentLoginTime;
        this.role = role;
        this.profileImage = profileImage;
        this.updateNicknameTime = updateNicknameTime;
    }

    public void updateMemberLastLoginTime() {
        this.recentLoginTime = LocalDateTime.now();
    }

    public void updateMemberNickname(String nickname) {
        this.nickname = nickname;
        this.updateNicknameTime = LocalDateTime.now();
    }
}
