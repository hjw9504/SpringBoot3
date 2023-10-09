package com.example.jdkproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "member")
public class MemberVo {
    @Id
    @Column(name = "member_id")
    private String memberId;
    private String userId;
    @JsonIgnore
    private String userPw;
    private String name;
    private String email;
    private String phone;
    private String nickname;
    private String registerTime;
    private String recentLoginTime;

    @Builder
    public MemberVo(String memberId, String userId, String userPw, String name, String email, String phone, String nickname, String registerTime, String recentLoginTime) {
        this.memberId = memberId;
        this.userId = userId;
        this.userPw = userPw;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.nickname = nickname;
        this.registerTime = registerTime;
        this.recentLoginTime = recentLoginTime;
    }
}
