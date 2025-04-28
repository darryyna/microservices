package edu.lytvyniuk.DTOs;

/*
  @author darin
  @project microservices
  @class UserDTO
  @version 1.0.0
  @since 28.04.2025 - 12.57
*/

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {

    private Long userId;
    @NotEmpty
    private String username;
    @Email
    private String email;
    @NotNull
    private String password;

    public UserDTO(Long userId, String username, String email, String password) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}