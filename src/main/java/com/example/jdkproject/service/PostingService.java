package com.example.jdkproject.service;

import com.example.jdkproject.domain.Posting;
import com.example.jdkproject.entity.MemberVo;
import com.example.jdkproject.entity.PostingResultProjection;
import com.example.jdkproject.entity.PostingVo;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.repository.PostingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostingService {
    private final PostingRepository postingRepository;

    public List<PostingResultProjection> getAllPost() {
        List<PostingResultProjection> postingVos = postingRepository.findAllPosting();
        if (postingVos == null || postingVos.size()==0) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        return postingVos;
    }

    public PostingResultProjection getPostByPostingId(int postingId) {
        PostingResultProjection postingVos = postingRepository.findPostingDetailByPostingId(postingId);
        if (postingVos == null) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        return postingVos;
    }

    public List<PostingResultProjection> getPostByMemberId(String memberId) {
        List<PostingResultProjection> postingVos = postingRepository.findPostingDetailByMemberId(memberId);
        if (postingVos == null) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        return postingVos;
    }

    public void saveNewPost(Posting posting) {
        PostingVo postingVo = new PostingVo(posting.getTitle(), posting.getBody(), new MemberVo(posting.getMemberId()), LocalDateTime.now().toString());
        Object registerResult = postingRepository.save(postingVo);
        if (registerResult == null) {
            log.error("DB Insert Error");
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }

        return;
    }

    public void updatePost(Posting posting) {
        Object registerResult = postingRepository.updateMemberPost(posting.getId(), posting.getTitle(),
                posting.getBody(), LocalDateTime.now().toString());
        if (registerResult == null) {
            log.error("DB Insert Error");
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }

        return;
    }
}
