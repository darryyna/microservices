package edu.lytvyniuk.DTOs;

/*
  @author darin
  @project microservices
  @class PreferenceDTO
  @version 1.0.0
  @since 28.04.2025 - 14.15
*/

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
public class PreferenceDTO {
    private Long preferenceId;
    @NotEmpty
    private String username;
    @NotEmpty
    private String genreName;

    @PositiveOrZero
    private Integer preferredMaxDuration;
    private Integer preferredMinYear;
    private Integer preferredMaxYear;
    @PositiveOrZero
    @Max(10)
    private Double preferredMaxRating;
}
