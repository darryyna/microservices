package edu.lytvyniuk.config;

/*
  @author darin
  @project microservices
  @class KafkaProducerController
  @version 1.0.0
  @since 04.05.2025 - 23.25
*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaProducerController {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.name}")
    private String topicName;

    @PostMapping("/kafka/publish")
    public String publishMessage(@RequestBody String message) {
        kafkaTemplate.send(topicName, message);
        return "Message sent to Kafka topic: " + topicName;
    }
}
