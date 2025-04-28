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

    // Маппінг DTO -> Entity
    // preferenceId, userId, genreId ігноруємо - вони встановлюються в сервісі
    @Mapping(target = "preferenceId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "genreId", ignore = true)
    Preference toEntity(PreferenceDTO preferenceDTO);

    // Маппінг Entity -> DTO (якщо потрібен простий маппінг без імен/назв)
    // Якщо потрібен DTO з username та genreName, використовуйте метод PreferenceService.toDTO()
    @Mapping(target = "username", ignore = true) // username отримується в сервісі
    @Mapping(target = "genreName", ignore = true) // genreName отримується в сервісі
    PreferenceDTO toBasicDTO(Preference preference);

}
