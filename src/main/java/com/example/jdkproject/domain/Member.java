package com.example.jdkproject.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
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
}
