package com.example.jdkproject.service;

import com.example.jdkproject.domain.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
public class KafkaProducer {

    private ObjectMapper objectMapper = new ObjectMapper();
    private String topicName = "auth_login";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(Member message) {
        log.info("Produce message : {} {}", message.getUserId(), message.getName());
        this.kafkaTemplate.send(topicName, getFilteredMap(message));
    }

    private String getFilteredMap(Member member) {
        try {
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("memberId", member.getMemberId());
            resultMap.put("name", member.getName());

            return objectMapper.writeValueAsString(resultMap);
        } catch(Exception e) {
            log.warn("Exception: ", e);
            return null;
        }
    }
}
