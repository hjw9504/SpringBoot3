package com.example.jdkproject.entity;

import com.example.jdkproject.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "posting")
public class PostingVo {
    @Id
    private int id;
    private String title;
    private String body;
    private String registerTime;
    private String modTime;
    @OneToOne
    @JoinColumn(name = "member_id")
    private MemberVo member;

    public PostingVo(String title, String body, MemberVo member, String registerTime) {
        this.member = member;
        this.title = title;
        this.body = body;
        this.registerTime = registerTime;
    }
}
