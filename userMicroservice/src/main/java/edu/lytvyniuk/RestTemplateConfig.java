package edu.lytvyniuk;

/*
  @author darin
  @project microservices
  @class RestTemplateConfig
  @version 1.0.0
  @since 15.04.2025 - 01.44
*/

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
