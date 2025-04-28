package edu.lytvyniuk.DTOs;

/*
  @author darin
  @project microservices
  @class RatingDTO
  @version 1.0.0
  @since 28.04.2025 - 13.12
*/

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class RatingDTO {
    private Long ratingId;
    @NotNull
    @DecimalMax("10.0")
    private Double score;
    private String comment;
    @PastOrPresent
    private LocalDateTime ratingDate;
    @NotEmpty
    private String username;
    @NotEmpty
    private String movieTitle;

}