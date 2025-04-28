package edu.lytvyniuk.DTOs;

/*
  @author darin
  @project microservices
  @class MovieDTO
  @version 1.0.0
  @since 28.04.2025 - 13.41
*/

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Data
public class MovieDTO {
    private Long movieId;
    @NotEmpty
    private String title;
    @NotNull
    private String description;
    private LocalDate releaseDate;
    @NotNull
    private Integer duration;
    private Double averageRating;
    private List<GenreDTO> genres;
}
