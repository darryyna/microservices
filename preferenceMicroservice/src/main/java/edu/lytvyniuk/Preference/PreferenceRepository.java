package edu.lytvyniuk.Preference;

/*
  @author darin
  @project microservices
  @class PreferenceRepository
  @version 1.0.0
  @since 28.04.2025 - 14.11
*/
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PreferenceRepository extends JpaRepository<Preference, Long> {
    List<Preference> findByUserId(Long userId);

    Optional<Preference> findByUserIdAndGenreId(Long userId, Long genreId);

    boolean existsByUserIdAndGenreId(Long userId, Long genreId);

}