package edu.lytvyniuk.Recommendation;

/*
  @author darin
  @project microservices
  @class RecommendationRepository
  @version 1.0.0
  @since 28.04.2025 - 14.31
*/
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByUserId(Long userId);
    Optional<Recommendation> findByUserIdAndMovieId(Long userId, Long movieId);
    boolean existsByUserIdAndMovieId(Long userId, Long movieId);
}
