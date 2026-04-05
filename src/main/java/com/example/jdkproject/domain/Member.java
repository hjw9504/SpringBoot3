package com.example.jdkproject.domain;

import com.example.jdkproject.entity.MemberVo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Member {
    private String memberId;
    private String userId;
    @JsonIgnore
    private String userPw;
    private String newUserPw;
    private String name;
    private String email;
    private String phone;
    private String nickname;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime registerTime;
    private LocalDateTime recentLoginTime;
    private String role;
    private String profileImage;
    private LocalDateTime updateNicknameTime;

    public Member(String userId, String name, String email, String phone, String nickname, LocalDateTime registerTime, LocalDateTime recentLoginTime, String role, LocalDateTime updateNicknameTime, String profileImage) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.nickname = nickname;
        this.registerTime = registerTime;
        this.recentLoginTime = recentLoginTime;
        this.role = role;
        this.updateNicknameTime = updateNicknameTime;
        this.profileImage = profileImage;
    }

    public Member toMember(MemberVo memberVo, String userId) {
        return Member.builder()
                .memberId(memberVo.getMemberId())
                .userId(userId)
                .name(memberVo.getName())
                .email(memberVo.getEmail())
                .phone(memberVo.getPhone())
                .nickname(memberVo.getNickname())
                .role(memberVo.getRole())
                .build();
    }
}
