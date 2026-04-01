package com.example.jdkproject.repository;

import com.example.jdkproject.entity.ChatRoomMemberVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMemberVo, Long> {

    Optional<ChatRoomMemberVo> findByRoomIdAndMemberId(long roomId, String memberId);
}
