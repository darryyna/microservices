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

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "movieTitle", ignore = true)
    RatingDTO toDTO(Rating rating);

    @Mapping(target = "ratingId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "movieId", ignore = true)
    Rating toEntity(RatingDTO ratingDTO);

    @Mapping(target = "ratingId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "movieId", ignore = true)
    void updateEntityFromDTO(RatingDTO ratingDTO, @MappingTarget Rating rating);


    List<RatingDTO> toDTOList(List<Rating> ratings);
}