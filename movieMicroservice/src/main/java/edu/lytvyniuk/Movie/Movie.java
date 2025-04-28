package edu.lytvyniuk.Movie;

/*
  @author darin
  @project microservices
  @class Movie
  @version 1.0.0
  @since 28.04.2025 - 13.40
*/
import edu.lytvyniuk.Movie.MovieGenre.MovieGenre;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long movieId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "average_rating")
    private Double averageRating; // Зберігаємо, але його оновлення потребує взаємодії з rating-service

    // Залишаємо, якщо Genre та MovieGenre керуються movie-service
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true) // Додано cascade та orphanRemoval
    private Set<MovieGenre> movieGenres = new HashSet<>();

    // Видаляємо зв'язок з Rating, оскільки Rating знаходиться в іншому сервісі
    // @OneToMany(mappedBy = "movie")
    // private Set<Rating> ratings = new HashSet<>();

    // Допоміжні методи для управління колекцією MovieGenre (важливо при bidirectional зв'язках)
    public void addMovieGenre(MovieGenre movieGenre) {
        movieGenres.add(movieGenre);
        movieGenre.setMovie(this);
    }

    public void removeMovieGenre(MovieGenre movieGenre) {
        movieGenres.remove(movieGenre);
        movieGenre.setMovie(null);
    }
}
