package edu.lytvyniuk.Recommendation;

/*
  @author darin
  @project microservices
  @class Recommendation
  @version 1.0.0
  @since 28.04.2025 - 14.31
*/

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id")
    private Long recommendationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @Column(name = "recommendation_score", nullable = false)
    private Double recommendationScore;

    @Column(name = "is_viewed")
    private Boolean isViewed = false;
}