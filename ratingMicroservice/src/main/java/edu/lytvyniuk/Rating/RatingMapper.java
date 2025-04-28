package edu.lytvyniuk.Rating;

/*
  @author darin
  @project microservices
  @class RatingMapper
  @version 1.0.0
  @since 28.04.2025 - 13.12
*/

import edu.lytvyniuk.DTOs.RatingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    // Маппінг Entity -> DTO
    // Не мапаємо username та movieTitle напряму, їх потрібно буде отримати окремо
    // або в іншому шарі.
    @Mapping(target = "username", ignore = true) // Ігноруємо, оскільки немає прямого User об'єкта
    @Mapping(target = "movieTitle", ignore = true) // Ігноруємо, оскільки немає прямого Movie об'єкта
    RatingDTO toDTO(Rating rating);

    // Маппінг DTO -> Entity
    // score, comment, ratingDate мапаються напряму
    // userId та movieId будуть встановлені в сервісі після отримання їх з інших сервісів
    @Mapping(target = "ratingId", ignore = true) // ID генерується базою
    @Mapping(target = "userId", ignore = true) // Встановлюється в сервісі
    @Mapping(target = "movieId", ignore = true) // Встановлюється в сервісі
    Rating toEntity(RatingDTO ratingDTO);

    // Якщо потрібно оновити Entity з DTO (без зміни ID користувача та фільму)
    @Mapping(target = "ratingId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "movieId", ignore = true)
    void updateEntityFromDTO(RatingDTO ratingDTO, @MappingTarget Rating rating);


    List<RatingDTO> toDTOList(List<Rating> ratings);

    // Методи findUserByUsername та findMovieByTitle більше не потрібні в маппері
    // Вони винесені в сервіс з використанням RestTemplate.
}