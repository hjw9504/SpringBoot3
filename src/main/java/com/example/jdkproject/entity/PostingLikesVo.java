package com.example.jdkproject.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "posting_likes_log")
public class PostingLikesVo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private int postingId;
    private String memberId;
    private LocalDateTime registerTime;

    @Builder
    public PostingLikesVo(int postingId, String memberId) {
        this.postingId = postingId;
        this.memberId = memberId;
        this.registerTime = LocalDateTime.now();
    }
}
