package com.example.jdkproject.repository;

import com.example.jdkproject.entity.MemberSecureVo;
import com.example.jdkproject.entity.MemberVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberSecureRepository extends JpaRepository<MemberSecureVo, Long> {
    MemberSecureVo findInfoByMemberId(String memberId);
}
