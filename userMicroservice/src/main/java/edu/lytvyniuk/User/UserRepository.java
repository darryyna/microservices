package edu.lytvyniuk.User;

/*
  @author darin
  @project microservices
  @class UserRepository
  @version 1.0.0
  @since 28.04.2025 - 12.59
*/

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}