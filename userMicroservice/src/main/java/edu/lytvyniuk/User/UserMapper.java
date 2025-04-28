package edu.lytvyniuk.User;

/*
  @author darin
  @project microservices
  @class UserMapper
  @version 1.0.0
  @since 28.04.2025 - 13.01
*/

import edu.lytvyniuk.DTOs.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);

    @Mapping(target = "userId", ignore = true)
    User toEntity(UserDTO userDTO);

    List<UserDTO> toDTOList(List<User> users);
}