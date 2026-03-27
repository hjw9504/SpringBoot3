package com.example.jdkproject.repository;

import com.example.jdkproject.entity.CommentResultProjection;
import com.example.jdkproject.entity.PostingCommentVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostingCommentRepository extends JpaRepository<PostingCommentVo, Long> {

    @Query(value = """
            SELECT c.comment, c.member_id as memberId, c.register_time as registerTime, m.user_id
            FROM posting_comment c
            INNER JOIN posting p ON c.posting_id = p.id
            INNER JOIN member m ON c.member_id = m.member_id
            WHERE c.posting_id = :postingId
            """, nativeQuery = true)
    List<CommentResultProjection> findAllWithMemberInfo(int postingId);
}