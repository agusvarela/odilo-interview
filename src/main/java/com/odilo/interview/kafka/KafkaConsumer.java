package com.odilo.interview.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class KafkaConsumer {

    @KafkaListener(topics = "user_events", groupId = "1")
    public void receiveMessage(ConsumerRecord<String, String> record) {
        String key = record.key();
        String message = record.value();
        log.info("[kafka-listener] - Event received. KEY: [{}]. MESSAGE: [{}]", key, message);
    }
}
