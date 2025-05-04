package edu.lytvyniuk.config;

/*
  @author darin
  @project microservices
  @class KafkaConfig
  @version 1.0.0
  @since 04.05.2025 - 23.22
*/

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic movieTopic() {
        return TopicBuilder.name("movie-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
