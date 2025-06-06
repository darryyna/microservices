package edu.lytvyniuk.Preference;

/*
  @author darin
  @project microservices
  @class Preference
  @version 1.0.0
  @since 28.04.2025 - 14.11
*/

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "preferences", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "genre_id"})
})
public class Preference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "preference_id")
    private Long preferenceId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "genre_id", nullable = false)
    private Long genreId;

    @Column(name = "preferred_duration")
    private Integer preferredMaxDuration;

    @Column(name = "preferred_min_year")
    private Integer preferredMinYear;

    @Column(name = "preferred_max_year")
    private Integer preferredMaxYear;

    @Column(name = "preferred_max_rating")
    private Double preferredMaxRating;
}
