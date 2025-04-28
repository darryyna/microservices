package edu.lytvyniuk.Movie.Genre;

/*
  @author darin
  @project microservices
  @class GenreMapper
  @version 1.0.0
  @since 28.04.2025 - 13.58
*/

import edu.lytvyniuk.DTOs.GenreDTO;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    GenreDTO toDTO(Genre genre);
    Genre toEntity(GenreDTO genreDTO);
    List<GenreDTO> toDTOList(List<Genre> genres);
}

