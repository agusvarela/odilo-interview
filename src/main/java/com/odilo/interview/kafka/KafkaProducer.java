package com.odilo.interview.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class KafkaProducer {

    public static final String USER_EVENTS = "user_events";

    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String key, String message) {
        log.info("[kafkaProducer] - Sending event. Topic: {}. Key: {}. Message: {}", USER_EVENTS, key, message);
        kafkaTemplate.send(USER_EVENTS, key, message);
    }

}
