package com.example.jdkproject.repository;

import com.example.jdkproject.entity.PostingLikesVo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostingLikesRepository extends JpaRepository<PostingLikesVo, Long> {
    Optional<PostingLikesVo> findByPostingIdAndMemberId(Integer postingId, String memberId);

    Optional<List<PostingLikesVo>> findByPostingId(Integer postingId);

    @Transactional // 삭제 작업에는 반드시 트랜잭션이 필요합니다.
    void deleteByPostingIdAndMemberId(int postingId, String memberId);
}