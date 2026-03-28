package com.example.jdkproject.dto;

import com.example.jdkproject.entity.MemberVo;
import com.example.jdkproject.entity.PostingLikesVo;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

@Getter
@Builder
public class PostingLikesDto {
    String memberId;
    int postingId;
    String userId;
    String profileImage;

    public static PostingLikesDto from(PostingLikesVo postingLikesVo, MemberVo memberVo) {
        return PostingLikesDto.builder()
                .postingId(postingLikesVo.getPostingId())
                .memberId(postingLikesVo.getMemberId())
                .userId(ObjectUtils.isNotEmpty(memberVo) ? memberVo.getUserId() : null)
                .profileImage(ObjectUtils.isNotEmpty(memberVo) ? memberVo.getProfileImage() : null)
                .build();
    }
}