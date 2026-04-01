package com.example.jdkproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메시지 구독 요청 경로 (브로커가 처리)
        config.enableSimpleBroker("/sub");
        // 메시지 발행 요청 경로 (컨트롤러가 처리)
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 웹소켓 연결 포인트: ws://localhost:8080/ws-chat
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*") // 테스트용으로 전체 허용
                .withSockJS(); // 낮은 버전 브라우저 호환성
    }
}