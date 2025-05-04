package edu.lytvyniuk.Movie.MovieGenre;

/*
  @author darin
  @project microservices
  @class MovieGenreMapper
  @version 1.0.0
  @since 28.04.2025 - 13.57
*/

import edu.lytvyniuk.DTOs.GenreDTO;
import edu.lytvyniuk.DTOs.MovieGenreDTO;
import edu.lytvyniuk.Movie.Genre.Genre;
import edu.lytvyniuk.Movie.Genre.GenreService;
import edu.lytvyniuk.Movie.Movie;
import edu.lytvyniuk.Movie.MovieService;
import edu.lytvyniuk.customException.ResourceNotFoundException;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MovieGenreMapper {

    @Mapping(source = "movie.title", target = "movieTitle")
    @Mapping(source = "genres", target = "genres", qualifiedByName = "genresToDTOs")
    MovieGenreDTO toDTO(Movie movie, List<Genre> genres);

    @Named("genresToDTOs")
    default List<GenreDTO> genresToDTOs(List<Genre> genres) {
        if (genres == null) {
            return null;
        }
        return genres.stream()
                .map(genre -> {
                    GenreDTO genreDTO = new GenreDTO();
                    genreDTO.setName(genre.getName());
                    genreDTO.setGenreId(genre.getGenreId());
                    return genreDTO;
                })
                .collect(Collectors.toList());
    }

    @Mapping(source = "movieTitle", target = "movie", qualifiedByName = "movieTitleToMovie")
    @Mapping(source = "genres", target = "genre", qualifiedByName = "genreDTOsToGenre")
    MovieGenre toEntity(MovieGenreDTO movieGenreDTO, @Context MovieService movieService, @Context GenreService genreService) throws ResourceNotFoundException;;

    @Named("movieTitleToMovie")
    default Movie movieTitleToMovie(String movieTitle, @Context MovieService movieService) throws ResourceNotFoundException {
        List<Movie> movies = movieService.findByTitle(movieTitle);
        if (movies != null && !movies.isEmpty()) {
            return movies.get(0);
        } else {
            return null;
        }
    }

    @Named("genreDTOsToGenre")
    default Genre genreDTOsToGenre(List<GenreDTO> genreDTOs, @Context GenreService genreService) throws ResourceNotFoundException {
        if (genreDTOs == null || genreDTOs.isEmpty()) {
            return null;
        }
        return genreService.findByName(genreDTOs.get(0).getName());
    }
}
