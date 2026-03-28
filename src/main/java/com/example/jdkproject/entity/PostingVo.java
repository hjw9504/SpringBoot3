package com.example.jdkproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "posting")
public class PostingVo {
    @Id
    private int id;
    private String title;
    private String body;
    private String registerTime;
    private String modTime;
    private long likes;
    @OneToOne
    @JoinColumn(name = "member_id")
    private MemberVo member;

    public PostingVo(String title, String body, MemberVo member, String registerTime) {
        this.member = member;
        this.title = title;
        this.body = body;
        this.registerTime = registerTime;
    }

    public void increasePostLikes() {
        this.likes++;
    }

    public void decreasePostLikes() {
        this.likes--;
    }
}
