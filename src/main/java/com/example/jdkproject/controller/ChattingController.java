package com.example.jdkproject.controller;

import com.example.jdkproject.annotation.TokenCheck;
import com.example.jdkproject.domain.Response;
import com.example.jdkproject.dto.ChattingDto;
import com.example.jdkproject.dto.JtiInfo;
import com.example.jdkproject.entity.ChatLogResultProjection;
import com.example.jdkproject.entity.ChatRoomVo;
import com.example.jdkproject.service.ChattingService;
import com.example.jdkproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChattingController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChattingService chattingService;
    private final UserService userService;

    private final static int SUCCESS = 0;

    @TokenCheck
    @GetMapping(value = "/chat/room")
    public Response<List<ChatRoomVo>> getChattingRoomList(HttpServletRequest request,
                                                          @RequestParam(required = false) String memberId) {

        JtiInfo jtiInfo = (JtiInfo) request.getAttribute("jtiInfo");
        return new Response<>(chattingService.getChattingRoom(jtiInfo.getMemberId()), HttpStatus.OK, SUCCESS);
    }

    @TokenCheck
    @PostMapping(value = "/chat/room")
    public Response<Void> createChattingRoom(@RequestBody ChattingDto dto) {
        chattingService.createChattingRoom(dto);
        return new Response<>(HttpStatus.OK, SUCCESS);
    }

    @TokenCheck
    @GetMapping(value = "/chat/message")
    public Response<List<ChatLogResultProjection>> getChattingList(HttpServletRequest request,
                                                                   @RequestParam(value = "room_id") long roomId) {

        JtiInfo jtiInfo = (JtiInfo) request.getAttribute("jtiInfo");
        return new Response<>(chattingService.getChattingLog(roomId, jtiInfo.getMemberId()), HttpStatus.OK, SUCCESS);
    }

    @MessageMapping("/message")
    public void message(ChattingDto message) {
        log.info("Message : {}", message);

        switch (message.getType()) {
            case ENTER -> {
                if (chattingService.enterRoom(message)) {
                    message.setMessage(message.getSender() + "님이 입장하셨습니다.");
                }
            }
            case TALK -> {
                chattingService.saveChattingLog(message);
            }
            case LEAVE -> {
                message.setMessage(message.getSender() + "님이 퇴장하셨습니다.");
                chattingService.leaveRoom(message);
            }
        }

        // 해당 방을 구독 중인 모든 유저에게 메시지 전송
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }
}
