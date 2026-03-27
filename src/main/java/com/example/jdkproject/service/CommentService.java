package com.example.jdkproject.service;

import com.example.jdkproject.dto.CommentDto;
import com.example.jdkproject.entity.PostingCommentVo;
import com.example.jdkproject.entity.PostingVo;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.repository.PostingCommentRepository;
import com.example.jdkproject.repository.PostingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {

    private final PostingRepository postingRepository;
    private final PostingCommentRepository postingCommentRepository;

    public void registerPostComment(int postingId, String memberId, String comment) {

        // verify existing post
        PostingVo postingVo = postingRepository.findById(postingId);
        if (postingVo == null) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        PostingCommentVo commentDto = PostingCommentVo.builder()
                .postingId(postingId)
                .memberId(memberId)
                .comment(comment)
                .build();

        postingCommentRepository.save(commentDto);
    }
}
