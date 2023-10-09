package com.example.jdkproject.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Member {
    private String memberId;
    private String userId;
    @JsonIgnore
    private String userPw;
    private String name;
    private String email;
    private String phone;
    private String nickName;
    private String token;
}
