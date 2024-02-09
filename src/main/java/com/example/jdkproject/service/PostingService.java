package com.example.jdkproject.service;

import com.example.jdkproject.entity.PostingResultProjection;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.repository.PostingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PostingService {
    private final PostingRepository postingRepository;

    public PostingService(PostingRepository postingRepository) {
        this.postingRepository = postingRepository;
    }

    public List<PostingResultProjection> getAllPost() {
        List<PostingResultProjection> postingVos = postingRepository.findAllPosting();
        if (postingVos == null || postingVos.size()==0) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        return postingVos;
    }

    public PostingResultProjection getPostByMemberId(int postingId) {
        PostingResultProjection postingVos = postingRepository.findPostingDetailByMemberId(postingId);
        if (postingVos == null) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        return postingVos;
    }
}
