package com.example.jdkproject.repository;

import com.example.jdkproject.entity.PostingCommentVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostingCommentRepository extends JpaRepository<PostingCommentVo, Long> {
//    Optional<PostingCommentVo> findByPostingId(Integer postingId, String memberId);
}