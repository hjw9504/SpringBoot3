package com.example.jdkproject.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "posting_comment")
public class PostingCommentVo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private int postingId;
    private String memberId;
    private String comment;
    private LocalDateTime registerTime;

    @Builder
    public PostingCommentVo(int postingId, String memberId, String comment) {
        this.postingId = postingId;
        this.memberId = memberId;
        this.comment = comment;
        this.registerTime = LocalDateTime.now();
    }
}
