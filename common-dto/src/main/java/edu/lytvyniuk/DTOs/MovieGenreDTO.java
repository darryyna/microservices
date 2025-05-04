package edu.lytvyniuk.DTOs;

/*
  @author darin
  @project microservices
  @class MovieGenreDTO
  @version 1.0.0
  @since 28.04.2025 - 13.53
*/

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
public class MovieGenreDTO {
    @NotEmpty
    private String movieTitle;
    @NotEmpty
    private List<GenreDTO> genres;

}
