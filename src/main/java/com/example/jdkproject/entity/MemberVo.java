package com.example.jdkproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "member")
public class MemberVo {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
