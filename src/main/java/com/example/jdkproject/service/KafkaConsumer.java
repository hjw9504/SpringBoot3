package com.example.jdkproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class KafkaConsumer {

//    @KafkaListener(topics = "myTestTopic", groupId = "testgroup")
    public void consume(String message) throws IOException {
        log.info("Message Received From Kafka Server: {} login", message);
    }
}
