package edu.lytvyniuk.User;

/*
  @author darin
  @project microservices
  @class UserController
  @version 1.0.0
  @since 28.04.2025 - 13.01
*/

import edu.lytvyniuk.DTOs.UserDTO;
import edu.lytvyniuk.customException.DuplicateResourceException;
import edu.lytvyniuk.customException.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RefreshScope
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    @Value("${project.title:Default Project Title}")
    private String projectTitle;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userMapper.toDTOList(userService.findAll());
    }

    @GetMapping("/project-title")
    public String getProjectTitleFromConfig() {
        return "Project Title from Config Server: " + projectTitle;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable(name = "id") Long id) throws ResourceNotFoundException {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(userMapper.toDTO(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable(name = "username") String username) throws ResourceNotFoundException {
        return userService.findByUsername(username)
                .map(user -> ResponseEntity.ok(userMapper.toDTO(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) throws DuplicateResourceException {
        User user = userMapper.toEntity(userDTO);
        User savedUser = userService.save(user);
        return ResponseEntity.ok(userMapper.toDTO(savedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") Long id) throws ResourceNotFoundException {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}