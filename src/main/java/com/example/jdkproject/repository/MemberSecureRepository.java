package com.example.jdkproject.repository;

import com.example.jdkproject.entity.MemberSecureVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberSecureRepository extends JpaRepository<MemberSecureVo, Long> {
    MemberSecureVo findInfoByMemberId(String memberId);
}
