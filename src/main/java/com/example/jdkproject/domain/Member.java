package com.example.jdkproject.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
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
    private String nickName;
    private String token;
    private String registerTime;
    private String recentLoginTime;

    @Builder
    public Member(String userId, String name, String email, String phone, String nickName, String registerTime, String recentLoginTime) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.nickName = nickName;
        this.registerTime = registerTime;
        this.recentLoginTime = recentLoginTime;
    }
}
