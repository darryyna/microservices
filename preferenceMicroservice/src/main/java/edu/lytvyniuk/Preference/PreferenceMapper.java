package edu.lytvyniuk.Preference;

/*
  @author darin
  @project microservices
  @class PreferenceMapper
  @version 1.0.0
  @since 28.04.2025 - 14.11
*/

import edu.lytvyniuk.DTOs.PreferenceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PreferenceMapper {

    @Mapping(target = "preferenceId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "genreId", ignore = true)
    Preference toEntity(PreferenceDTO preferenceDTO);


    @Mapping(target = "username", ignore = true)
    @Mapping(target = "genreName", ignore = true)
    PreferenceDTO toBasicDTO(Preference preference);

}
