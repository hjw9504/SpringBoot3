package com.example.jdkproject.repository;

import com.example.jdkproject.entity.FollowerResultProjection;
import com.example.jdkproject.entity.MemberFollowerVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberFollowerRepository extends JpaRepository<MemberFollowerVo, Long> {

    @Query(value = """
            select f.member_id as memberId, f.follow_member_id as followMemberId, f.register_time as registerTime, f.followed_time as followedTime,
            m.user_id as userId, m2.user_id as followUserId,
            m.profile_image as profileImage, m2.profile_image as followProfileImage from member_follower f
            inner join member m on f.member_id = m.member_id
            inner join member m2 on f.follow_member_id = m2.member_id
            where f.member_id = :memberId
            """, nativeQuery = true)
    Optional<List<FollowerResultProjection>> findFollowerByMemberId(String memberId);

    @Query(value = """
            select f.member_id as memberId, f.follow_member_id as followMemberId, f.register_time as registerTime, f.followed_time as followedTime,
            m.user_id as userId, m2.user_id as followUserId,
            m.profile_image as profileImage, m2.profile_image as followProfileImage from member_follower f
            inner join member m on f.member_id = m.member_id
            inner join member m2 on f.follow_member_id = m2.member_id
            where f.member_id = :memberId and f.follow_member_id = :followId
            """, nativeQuery = true)
    Optional<FollowerResultProjection> findFollowerByMemberIdAndFollowMemberId(String memberId, String followId);

    @Modifying(clearAutomatically = true)
    @Query("update member_follower m set m.followedTime = :followedTime where m.memberId = :memberId and m.followMemberId = :followMemberId")
    void updateFollowTimeByMemberIdAndFollowId(LocalDateTime followedTime, String memberId, String followMemberId);

    int deleteByMemberIdAndFollowMemberId(String memberId, String followId);
}