package com.example.jdkproject.controller;

import com.example.jdkproject.domain.Response;
import com.example.jdkproject.dto.ChattingDto;
import com.example.jdkproject.dto.JtiInfo;
import com.example.jdkproject.entity.ChatLogResultProjection;
import com.example.jdkproject.entity.ChatLogVo;
import com.example.jdkproject.entity.ChatRoomVo;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.service.ChattingService;
import com.example.jdkproject.service.UserService;
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

    @GetMapping(value = "/chat/room")
    public Response<List<ChatRoomVo>> getChattingRoomList(@RequestHeader String token, @RequestParam(required = false) String memberId) {
        try {
            userService.verifyToken(token);
            return new Response<>(chattingService.getChattingRoom(memberId), HttpStatus.OK, SUCCESS);
        } catch (CommonErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }

    }

    @PostMapping(value = "/chat/room")
    public Response<Void> createChattingRoom(@RequestHeader String token, @RequestBody ChattingDto dto) {
        try {
            userService.verifyToken(token);
            chattingService.createChattingRoom(dto);
            return new Response<>(HttpStatus.OK, SUCCESS);
        } catch (CommonErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }
    }

    @GetMapping(value = "/chat/message")
    public Response<List<ChatLogResultProjection>> getChattingList(@RequestHeader String token, @RequestParam(value = "room_id") long roomId) {
        try {
            JtiInfo jtiInfo = userService.verifyToken(token);
            return new Response<>(chattingService.getChattingLog(roomId, jtiInfo.getMemberId()), HttpStatus.OK, SUCCESS);
        } catch (CommonErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }

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
