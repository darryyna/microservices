package edu.lytvyniuk.Recommendation;

/*
  @author darin
  @project microservices
  @class RecommendationMapper
  @version 1.0.0
  @since 28.04.2025 - 14.32
*/

import edu.lytvyniuk.DTOs.RecommendationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    @Mapping(target = "recommendationId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "movieId", ignore = true)
    Recommendation toEntity(RecommendationDTO recommendationDTO);

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "movieTitle", ignore = true)
    RecommendationDTO toBasicDTO(Recommendation recommendation);
}
