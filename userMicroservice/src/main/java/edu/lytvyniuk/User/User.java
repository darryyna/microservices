package edu.lytvyniuk.User;

/*
  @author darin
  @project microservices
  @class User
  @version 1.0.0
  @since 15.04.2025 - 01.25
*/

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    // Removed @OneToMany relationships to Rating, Preference, Recommendation
    // These entities now reside in their own microservices.
}