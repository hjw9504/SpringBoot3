package com.example.jdkproject.service;

import com.example.jdkproject.dto.ChattingDto;
import com.example.jdkproject.entity.ChatLogVo;
import com.example.jdkproject.entity.ChatRoomMemberVo;
import com.example.jdkproject.entity.ChatRoomVo;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.repository.ChatLogRepository;
import com.example.jdkproject.repository.ChatRoomMemberRepository;
import com.example.jdkproject.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChattingService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatLogRepository chatLogRepository;

    public List<ChatRoomVo> getChattingRoom(String memberId) {
        List<ChatRoomVo> chatRoomVo = chatRoomRepository.findAll();
        return chatRoomVo;
    }

    public List<ChatLogVo> getChattingLog(long roomId, String memberId) {
        List<ChatLogVo> chatLogVos = chatLogRepository.findByRoomId(roomId);

        LocalDateTime roomEnterTime;
        Optional<ChatRoomMemberVo> roomMemberVo = chatRoomMemberRepository.findByRoomIdAndMemberId(roomId, memberId);
        roomEnterTime = roomMemberVo.map(ChatRoomMemberVo::getRegisterTime).orElse(null);

        return chatLogVos.stream()
                .filter(vo -> vo.getRegisterTime().isAfter(roomEnterTime))
                .collect(Collectors.toList());
    }

    public void createChattingRoom(ChattingDto dto) {
        ChatRoomVo chatRoomVo = ChatRoomVo.builder()
                .name(dto.getRoomName())
                .registerTime(LocalDateTime.now())
                .build();

        chatRoomRepository.save(chatRoomVo);
    }

    public boolean enterRoom(ChattingDto dto) {

        Optional<ChatRoomVo> roomVo = chatRoomRepository.findById(dto.getRoomId());
        if (roomVo.isEmpty()) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        Optional<ChatRoomMemberVo> roomMemberVo = chatRoomMemberRepository.findByRoomIdAndMemberId(dto.getRoomId(), dto.getMemberId());
        if (roomMemberVo.isEmpty()) {
            ChatRoomMemberVo chatRoomMemberVo = ChatRoomMemberVo.builder()
                    .roomId(dto.getRoomId())
                    .memberId(dto.getMemberId())
                    .registerTime(LocalDateTime.now())
                    .build();

            chatRoomMemberRepository.save(chatRoomMemberVo);
            return true;
        }

        return false;
    }

    public void leaveRoom(ChattingDto dto) {

        Optional<ChatRoomVo> roomVo = chatRoomRepository.findById(dto.getRoomId());
        if (roomVo.isEmpty()) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        Optional<ChatRoomMemberVo> roomMemberVo = chatRoomMemberRepository.findByRoomIdAndMemberId(dto.getRoomId(), dto.getMemberId());
        roomMemberVo.ifPresent(chatRoomMemberRepository::delete);
    }

    @Async
    public void saveChattingLog(ChattingDto dto) {

        ChatLogVo vo = ChatLogVo.builder()
                .roomId(dto.getRoomId())
                .memberId(dto.getMemberId())
                .message(dto.getMessage())
                .registerTime(LocalDateTime.now())
                .build();

        chatLogRepository.save(vo);
    }
}
