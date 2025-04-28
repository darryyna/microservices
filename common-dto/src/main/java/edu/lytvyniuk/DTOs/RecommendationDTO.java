package edu.lytvyniuk.DTOs;

/*
  @author darin
  @project microservices
  @class RecommendationDTO
  @version 1.0.0
  @since 28.04.2025 - 14.33
*/

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class RecommendationDTO {
    private Long recommendationId;
    @NotEmpty
    private String username;
    @NotEmpty
    private String movieTitle;
    @NotNull
    @Max(10)
    private Double recommendationScore;
    private Boolean isViewed = false;
}