package edu.lytvyniuk.DTOs;

/*
  @author darin
  @project microservices
  @class GenreDTO
  @version 1.0.0
  @since 28.04.2025 - 13.52
*/
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@NoArgsConstructor
public class GenreDTO {
    private Long genreId;
    @NotEmpty
    private String name;

    public GenreDTO(Long genreId, String name) {
        this.genreId = genreId;
        this.name = name;
    }

    public GenreDTO(String name) {
        this.name = name;
    }
}
