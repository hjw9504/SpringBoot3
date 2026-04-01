package com.example.jdkproject.repository;

import com.example.jdkproject.entity.ChatLogVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatLogRepository extends JpaRepository<ChatLogVo, Long> {

    List<ChatLogVo> findByRoomId(long roomId);
}
