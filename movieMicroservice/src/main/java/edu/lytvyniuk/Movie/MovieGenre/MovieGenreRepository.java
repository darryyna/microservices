package edu.lytvyniuk.Movie.MovieGenre;

/*
  @author darin
  @project microservices
  @class MovieGenreRepository
  @version 1.0.0
  @since 28.04.2025 - 13.55
*/

import edu.lytvyniuk.Movie.Genre.Genre;
import edu.lytvyniuk.Movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MovieGenreRepository extends JpaRepository<MovieGenre, Long> {
    @Query("SELECT g FROM Genre g JOIN MovieGenre mg ON mg.genre.genreId = g.genreId WHERE mg.movie.movieId = :movieId")
    List<Genre> findGenresByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT mg.movie FROM MovieGenre mg WHERE mg.genre.genreId = :genreId")
    List<Movie> findMoviesByGenreId(@Param("genreId") Long genreId);

    List<MovieGenre> findByMovie_MovieId(Long movieId);

    boolean existsByMovieAndGenre(Movie movie, Genre genre);
}

