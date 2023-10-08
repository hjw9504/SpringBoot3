package com.example.jdkproject.repository;

import com.example.jdkproject.entity.MemberVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<MemberVo, Long> {
    public List<MemberVo> findUserByMemberId(String memberId);

    public MemberVo findUserByUserId(String userId);
}
