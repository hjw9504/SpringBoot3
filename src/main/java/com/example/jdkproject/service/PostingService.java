package com.example.jdkproject.service;

import com.example.jdkproject.domain.Posting;
import com.example.jdkproject.dto.PostingDto;
import com.example.jdkproject.dto.PostingLikesDto;
import com.example.jdkproject.entity.MemberVo;
import com.example.jdkproject.entity.PostingLikesVo;
import com.example.jdkproject.entity.PostingResultProjection;
import com.example.jdkproject.entity.PostingVo;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.repository.MemberRepository;
import com.example.jdkproject.repository.PostingLikesRepository;
import com.example.jdkproject.repository.PostingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostingService {
    private final PostingRepository postingRepository;
    private final PostingLikesRepository postingLikesRepository;
    private final MemberRepository memberRepository;

    public List<PostingDto> getAllPost(String memberId) {
        List<PostingResultProjection> postingVos = postingRepository.findAllPosting();
        if (postingVos == null || postingVos.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        return postingVos.stream().map(p -> {
            // memberId의 좋아요 기록 조회
            Optional<PostingLikesVo> postingLikesVo = postingLikesRepository.findByPostingIdAndMemberId(p.getId(), memberId);

            return PostingDto.builder()
                    .id(p.getId())
                    .title(p.getTitle())
                    .body(p.getBody())
                    .likes(p.getLikes())
                    .registerTime(p.getRegisterTime())
                    .member(PostingDto.MemberInfo.builder()
                            .memberId(p.getMemberId())
                            .name(p.getName())
                            .profileImage(p.getProfileImage())
                            .isLikeTrue(postingLikesVo.isPresent())
                            .build())
                    .build();
        }).collect(Collectors.toList());
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
        postingRepository.save(postingVo);
    }

    public void updatePost(Posting posting) {
        postingRepository.updateMemberPost(posting.getId(), posting.getTitle(),
                posting.getBody(), LocalDateTime.now().toString());
    }

    public List<PostingLikesDto> getPostLikes(int postingId, String memberId) {
        List<PostingLikesVo> postingLikesVo = postingLikesRepository.findByPostingId(postingId)
                .orElseThrow(() -> new CommonErrorException(ErrorStatus.NOT_FOUND));

        return postingLikesVo.stream()
                .map(vo -> {
                    // 각 게시물의 memberId로 회원 정보를 조회
                    MemberVo member = memberRepository.findUserByMemberId(vo.getMemberId())
                            .filter(list -> !list.isEmpty())
                            .map(list -> list.get(0))
                            .orElse(null); // 회원이 없을 경우 null 처리 (혹은 기본값)

                    return PostingLikesDto.from(vo, member);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updatePostLikes(String memberId, int postingId, String type) {
        switch(type) {
            case "i" -> {
                Optional<PostingLikesVo> postingLikesVo = postingLikesRepository.findByPostingIdAndMemberId(postingId, memberId);
                if (postingLikesVo.isPresent()) {
                    throw new CommonErrorException(ErrorStatus.ALREADY_EXIST);
                }

                postingLikesRepository.save(new PostingLikesVo(postingId, memberId));

                PostingVo postingVo = postingRepository.findById(postingId);
                postingVo.increasePostLikes();
                postingRepository.save(postingVo);
            }
            case "d" -> {
                Optional<PostingLikesVo> postingLikesVo = postingLikesRepository.findByPostingIdAndMemberId(postingId, memberId);
                if (postingLikesVo.isEmpty()) {
                    throw new CommonErrorException(ErrorStatus.NOT_FOUND);
                }

                postingLikesRepository.deleteByPostingIdAndMemberId(postingId, memberId);

                PostingVo postingVo = postingRepository.findById(postingId);
                postingVo.decreasePostLikes();
                postingRepository.save(postingVo);
            }
            default -> throw new CommonErrorException(ErrorStatus.PARAMETER_NOT_FOUND);
        }
    }
}
