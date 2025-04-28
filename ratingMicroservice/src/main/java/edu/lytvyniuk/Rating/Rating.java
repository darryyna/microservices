package edu.lytvyniuk.Rating;

/*
  @author darin
  @project microservices
  @class Rating
  @version 1.0.0
  @since 28.04.2025 - 13.11
*/

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ratings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "movie_id"})
})
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long ratingId;

    @Column(name = "score", nullable = false)
    private Double score;

    @Column(name = "comment", length = 1000)
    private String comment;

    @Column(name = "rating_date")
    private LocalDateTime ratingDate;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "movie_id", nullable = false)
    private Long movieId;

}