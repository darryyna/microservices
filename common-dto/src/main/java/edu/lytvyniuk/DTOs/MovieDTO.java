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
    private Long movieId; // Додано ID
    @NotEmpty
    private String title;
    @NotNull
    private String description;
    private LocalDate releaseDate;
    @NotNull
    private Integer duration;
    // averageRating може бути в DTO, але його отримання/оновлення - окрема логіка
    private Double averageRating;
    // Залишаємо, якщо GenreDTO знаходиться у common-dto або тут
    private List<GenreDTO> genres;

    // Видаляємо зв'язок з RatingDTO
    // private List<RatingDTO> ratings;
}
