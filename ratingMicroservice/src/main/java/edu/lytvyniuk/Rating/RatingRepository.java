package edu.lytvyniuk.Rating;

/*
  @author darin
  @project microservices
  @class RatingRepository
  @version 1.0.0
  @since 28.04.2025 - 13.12
*/

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByMovieId(Long movieId);

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    Optional<Rating> findByUserIdAndMovieId(Long userId, Long movieId);

    List<Rating> findByUserId(Long userId);
}