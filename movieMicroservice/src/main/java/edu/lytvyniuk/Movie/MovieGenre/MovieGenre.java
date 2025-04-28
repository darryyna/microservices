package edu.lytvyniuk.Movie.MovieGenre;

/*
  @author darin
  @project microservices
  @class MovieGenre
  @version 1.0.0
  @since 28.04.2025 - 13.50
*/

import edu.lytvyniuk.Movie.Genre.Genre;
import edu.lytvyniuk.Movie.Movie;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "movie_genre")
public class MovieGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_genre_id")
    private Long movieGenreId;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

}
