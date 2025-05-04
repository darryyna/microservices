package edu.lytvyniuk.config;

/*
  @author darin
  @project microservices
  @class KafkaConsumer
  @version 1.0.0
  @since 04.05.2025 - 23.29
*/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "${kafka.topic.name}", groupId = "${kafka.consumer.group-id}")
    public void consume(String message) {
        logger.info("Received message: {}", message);
    }
}
