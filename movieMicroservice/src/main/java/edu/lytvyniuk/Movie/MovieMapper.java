package edu.lytvyniuk.Movie;

/*
  @author darin
  @project microservices
  @class MovieMapper
  @version 1.0.0
  @since 28.04.2025 - 13.42
*/

import edu.lytvyniuk.DTOs.GenreDTO;
import edu.lytvyniuk.DTOs.MovieDTO;
import edu.lytvyniuk.Movie.MovieGenre.MovieGenre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    @Mapping(source = "movieGenres", target = "genres", qualifiedByName = "movieGenresToDTOs")
    MovieDTO toDTO(Movie movie);

    @Mapping(target = "movieId", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "movieGenres", ignore = true)
    Movie toEntity(MovieDTO movieDTO);

    @Mapping(target = "movieId", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "movieGenres", ignore = true)
    void updateEntityFromDTO(MovieDTO movieDTO, @MappingTarget Movie movie);

    @Named("movieGenresToDTOs")
    default List<GenreDTO> movieGenresToDTOs(List<MovieGenre> movieGenres) {
        if (movieGenres == null) {
            return List.of();
        }
        return movieGenres.stream()
                .map(movieGenre -> movieGenre.getGenre())
                .filter(genre -> genre != null)
                .map(genre -> {
                    GenreDTO genreDTO = new GenreDTO();
                    genreDTO.setName(genre.getName());
                    return genreDTO;
                })
                .collect(Collectors.toList());
    }
}