package edu.lytvyniuk.User;

/*
  @author darin
  @project microservices
  @class UserService
  @version 1.0.0
  @since 28.04.2025 - 12.59
*/

import edu.lytvyniuk.customException.DuplicateResourceException;
import edu.lytvyniuk.customException.ResourceNotFoundException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RefreshScope
@Service
public class UserService {
    private final UserRepository userRepository;

    @Getter
    @Value("${project.title:Default Project Title}")
    private String projectTitle;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) throws ResourceNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User with username " + username + " not found");
        }
        return user;
    }

    public Optional<User> findById(Long id) throws ResourceNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
        return user;
    }

    public User save(User user) throws DuplicateResourceException {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new DuplicateResourceException("User with username " + user.getUsername() + " already exists");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateResourceException("User with email " + user.getEmail() + " already exists");
        }
        return userRepository.save(user);
    }

    public void deleteById(Long id) throws ResourceNotFoundException {
        if (userRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }
}