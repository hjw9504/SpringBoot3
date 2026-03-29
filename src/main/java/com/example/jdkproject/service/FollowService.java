package com.example.jdkproject.service;

import com.example.jdkproject.dto.MemberFollowDto;
import com.example.jdkproject.entity.FollowerResultProjection;
import com.example.jdkproject.entity.MemberFollowerVo;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.repository.MemberFollowerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FollowService {

    private final MemberFollowerRepository memberFollowerRepository;
    private boolean isFriend = false;

    public List<MemberFollowDto> getMemberFollowerList(String memberId) {
        Optional<List<FollowerResultProjection>> projections = memberFollowerRepository.findFollowerByMemberId(memberId);
        if (projections.isEmpty()) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        List<FollowerResultProjection> resultProjections = projections.get();

        return resultProjections.stream()
                .map(p -> MemberFollowDto.builder()
                        .memberId(p.getMemberId())
                        .followMemberId(p.getFollowMemberId())
                        .userId(p.getUserId())
                        .followUserId(p.getFollowUserId())
                        .registerTime(p.getRegisterTime())
                        .followedTime(p.getFollowedTime())
                        .profileImage(p.getProfileImage())
                        .followProfileImage(p.getFollowProfileImage())
                        .build())
                .toList();
    }

    @Transactional
    public void followMember(String memberId, String followerId) {

        // 이미 팔로우 했는지 체크
        Optional<FollowerResultProjection> result = memberFollowerRepository.findFollowerByMemberIdAndFollowMemberId(memberId, followerId);
        if (result.isPresent()) {
            throw new CommonErrorException(ErrorStatus.ALREADY_FOLLOW_MEMBER);
        }

        // 맞팔인지 체크 (나를 팔로우했는지 체크)
        // 팔로우 타임 업데이트
        isFriend = false;
        Optional<FollowerResultProjection> projection = memberFollowerRepository.findFollowerByMemberIdAndFollowMemberId(followerId, memberId);
        if (projection.isPresent()) {
            memberFollowerRepository.updateFollowTimeByMemberIdAndFollowId(LocalDateTime.now(), followerId, memberId);
            isFriend = true;
        }

        memberFollowerRepository.deleteByMemberIdAndFollowMemberId(memberId, followerId);

        MemberFollowerVo memberFollowerVo = MemberFollowerVo.builder()
                .memberId(memberId)
                .followMemberId(followerId)
                .registerTime(LocalDateTime.now())
                .followedTime(isFriend ? LocalDateTime.now() : null)
                .build();

        memberFollowerRepository.save(memberFollowerVo);
    }

    @Transactional
    public void unfollowMember(String memberId, String followerId) {

        // 이미 팔로우 했는지 체크
        Optional<FollowerResultProjection> result = memberFollowerRepository.findFollowerByMemberIdAndFollowMemberId(memberId, followerId);
        if (result.isEmpty()) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        // 맞팔인지 체크 (나를 팔로우했는지 체크)
        // 팔로우 타임 업데이트
        Optional<FollowerResultProjection> projection = memberFollowerRepository.findFollowerByMemberIdAndFollowMemberId(followerId, memberId);
        if (projection.isPresent()) {
            memberFollowerRepository.updateFollowTimeByMemberIdAndFollowId(null, followerId, memberId);
        }

        memberFollowerRepository.deleteByMemberIdAndFollowMemberId(memberId, followerId);
    }
}
