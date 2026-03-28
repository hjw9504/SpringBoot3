package com.example.jdkproject.repository;

import com.example.jdkproject.entity.PostingResultProjection;
import com.example.jdkproject.entity.PostingVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostingRepository extends JpaRepository<PostingVo, Long> {

    PostingVo findById(int postingId);

    @Query("SELECT p.id as id, m.memberId as memberId, p.title as title, m.profileImage as profileImage," +
            "p.body as body, p.registerTime as registerTime, p.modTime as modTime, p.likes as likes, m.name as name FROM posting p join " +
            "p.member m on p.member.memberId = m.memberId")
    List<PostingResultProjection> findAllPosting();

    @Query("SELECT p.id as id, m.memberId as memberId, p.title as title, m.profileImage as profileImage," +
            "p.body as body, p.registerTime as registerTime, p.modTime as modTime, p.likes as likes, m.name as name FROM posting p join " +
            "p.member m on p.member.memberId = :memberId")
    List<PostingResultProjection> findPostingDetailByMemberId(String memberId);

    @Query("SELECT p.id as id, m.memberId as memberId, p.title as title, m.profileImage as profileImage," +
            "p.body as body, p.registerTime as registerTime, p.modTime as modTime, p.likes as likes, m.name as name FROM posting p join " +
            "p.member m on p.member.memberId = m.memberId where p.id = :id")
    PostingResultProjection findPostingDetailByPostingId(int id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update posting p set p.title = :title, p.body = :body, p.modTime = :modTime where p.id = :id")
    int updateMemberPost(int id, String title, String body, String modTime);
}