package com.example.jdkproject.service;

import lombok.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private String topicName = "myTestTopic";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message) {
        System.out.println(String.format("Produce message : %s", message));
        this.kafkaTemplate.send(topicName, message);
    }
}
