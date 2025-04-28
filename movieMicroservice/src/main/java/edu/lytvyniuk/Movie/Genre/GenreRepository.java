package edu.lytvyniuk.Movie.Genre;

/*
  @author darin
  @project microservices
  @class GenreRepository
  @version 1.0.0
  @since 28.04.2025 - 13.54
*/

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    Genre findByName(String name);
}