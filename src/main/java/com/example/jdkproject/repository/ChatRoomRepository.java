package com.example.jdkproject.repository;

import com.example.jdkproject.entity.ChatRoomVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomVo, Long> {

}
