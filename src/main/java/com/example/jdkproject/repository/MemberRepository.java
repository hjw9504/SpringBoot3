package com.example.jdkproject.repository;

import com.example.jdkproject.entity.MemberVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<MemberVo, Long> {
    public List<MemberVo> findUserByMemberId(String memberId);

    public MemberVo findUserByUserId(String userId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update member m set m.recentLoginTime = :recentLoginTime where m.memberId = :memberId")
    int updateLastLoginTime(String memberId, String recentLoginTime);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update member m set m.userPw = :userPw where m.userId = :userId")
    int resetUserPassword(String userId, String userPw);
}
