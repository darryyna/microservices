package edu.lytvyniuk.Movie;

/*
  @author darin
  @project microservices
  @class MovieRepository
  @version 1.0.0
  @since 28.04.2025 - 13.41
*/

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByTitle(String title);

    Optional<Movie> findMovieByTitle(String title);
    @Query("SELECT m FROM Movie m WHERE m.averageRating >= :minRating")
    List<Movie> findByMinimumRating(Double minRating);

    boolean existsByTitleAndReleaseDate(String title, LocalDate releaseDate);
}
