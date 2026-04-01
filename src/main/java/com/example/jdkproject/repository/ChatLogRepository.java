package com.example.jdkproject.repository;

import com.example.jdkproject.entity.ChatLogResultProjection;
import com.example.jdkproject.entity.ChatLogVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatLogRepository extends JpaRepository<ChatLogVo, Long> {

    List<ChatLogVo> findByRoomId(long roomId);

    @Query(value = """
            select l.room_id as roomId, m.member_id as memberId, l.message, l.register_time as registerTime, m.user_id as userId from tb_chat_log l
            inner join member m on l.member_id = m.member_id
            where l.room_id = :roomId
            """, nativeQuery = true)
    List<ChatLogResultProjection> findByRoomIdAndUserId(long roomId);
}
