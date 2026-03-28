package com.example.jdkproject.repository;

import com.example.jdkproject.entity.MemberChannelVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberChannelRepository extends JpaRepository<MemberChannelVo, Long> {
    Optional<MemberChannelVo> findUserByIdpUserIdAndIdpType(String idpUserId, String idpType);

}
