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
import java.util.Set; // Використовуємо Set для movieGenres

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    // Маппінг Entity -> DTO
    // Мапаємо поля Movie напряму.
    // Поле genres мапаємо з Set<MovieGenre> якщо Genres керуються тут.
    // Поле ratings відсутнє в Entity та DTO (для цього сервісу).
    @Mapping(source = "movieGenres", target = "genres", qualifiedByName = "movieGenresToDTOs")
    MovieDTO toDTO(Movie movie);

    // Маппінг DTO -> Entity
    // movieId ігноруємо при створенні
    // averageRating, movieGenres ігноруємо при маппінгу з DTO (вони управляються сервісом)
    @Mapping(target = "movieId", ignore = true)
    @Mapping(target = "averageRating", ignore = true) // averageRating управляється сервісом
    @Mapping(target = "movieGenres", ignore = true) // movieGenres управляється сервісом
    Movie toEntity(MovieDTO movieDTO);

    // Метод для оновлення Entity з DTO
    @Mapping(target = "movieId", ignore = true)
    @Mapping(target = "averageRating", ignore = true) // averageRating управляється сервісом
    @Mapping(target = "movieGenres", ignore = true) // movieGenres управляється сервісом
    void updateEntityFromDTO(MovieDTO movieDTO, @MappingTarget Movie movie);


    // Маппінг Set<MovieGenre> -> List<GenreDTO>, якщо Genre керуються тут
    @Named("movieGenresToDTOs")
    default List<GenreDTO> movieGenresToDTOs(Set<MovieGenre> movieGenres) {
        if (movieGenres == null) {
            return List.of(); // Краще повертати порожній список замість null
        }
        return movieGenres.stream()
                .map(movieGenre -> movieGenre.getGenre()) // Отримуємо об'єкт Genre
                .filter(genre -> genre != null) // Фільтруємо null Genre об'єкти
                .map(genre -> { // Тепер працюємо тільки з не-null Genre
                    GenreDTO genreDTO = new GenreDTO();
                    genreDTO.setName(genre.getName());
                    // Якщо у GenreDTO є ID:
                    // genreDTO.setGenreId(genre.getGenreId());
                    return genreDTO;
                })
                .collect(Collectors.toList());
    }

    // Метод ratingsToDTOs більше не потрібен тут
    // @Named("ratingsToDTOs")
    // default List<RatingDTO> ratingsToDTOs(List<Rating> ratings) { ... }

}