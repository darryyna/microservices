package edu.lytvyniuk.DTOs;

import lombok.*;

/*
  @author darin
  @project microservices
  @class KafkaCreatedMovie
  @version 1.0.0
  @since 04.05.2025 - 23.36
*/

@AllArgsConstructor
@NoArgsConstructor
@Data
public class KafkaMovieTmpl {

        private Long movieId;
        private String title;
}
