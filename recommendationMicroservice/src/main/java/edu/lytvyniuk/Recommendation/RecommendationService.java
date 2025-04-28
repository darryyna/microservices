package edu.lytvyniuk.Recommendation;

/*
  @author darin
  @project microservices
  @class RecommendationService
  @version 1.0.0
  @since 28.04.2025 - 14.31
*/

import edu.lytvyniuk.DTOs.MovieDTO;
import edu.lytvyniuk.DTOs.RecommendationDTO;
import edu.lytvyniuk.DTOs.UserDTO;
import edu.lytvyniuk.customException.DuplicateResourceException;
import edu.lytvyniuk.customException.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference; // Може знадобитися для списків
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final RestTemplate restTemplate;

    @Value("http://localhost:9991")
    private String userServiceUrl;

    @Value("http://localhost:9992")
    private String movieServiceUrl;

    public RecommendationService(RecommendationRepository recommendationRepository, RestTemplate restTemplate) {
        this.recommendationRepository = recommendationRepository;
        this.restTemplate = restTemplate;
    }

    private Long getUserIdByUsername(String username) throws ResourceNotFoundException {
        String url = userServiceUrl + "/users/username/" + username;
        try {
            ResponseEntity<UserDTO> response = restTemplate.getForEntity(url, UserDTO.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().getUserId() != null) {
                return response.getBody().getUserId();
            } else {
                throw new ResourceNotFoundException("User ID not found in response for username: " + username);
            }
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        } catch (Exception e) {
            throw new RuntimeException("Error calling user service: " + e.getMessage(), e);
        }
    }

    private Long getMovieIdByTitle(String movieTitle) throws ResourceNotFoundException {
        String url = movieServiceUrl + "/movies/title/" + movieTitle;
        try {
            ResponseEntity<List<MovieDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<MovieDTO>>() {}
            );
            List<MovieDTO> movies = response.getBody();
            if (response.getStatusCode().is2xxSuccessful() && movies != null && !movies.isEmpty() && movies.get(0).getMovieId() != null) {
                return movies.get(0).getMovieId();
            } else {
                throw new ResourceNotFoundException("Movie ID not found in response for title: " + movieTitle);
            }

        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Movie not found with title: " + movieTitle);
        } catch (Exception e) {
            throw new RuntimeException("Error calling movie service: " + e.getMessage(), e);
        }
    }


    public List<Recommendation> findByUserId(Long userId) throws ResourceNotFoundException {
        List<Recommendation> recommendations = recommendationRepository.findByUserId(userId);
        if (recommendations.isEmpty()) {
            throw new ResourceNotFoundException("Recommendation not found for user with id: " + userId);
        }
        return recommendations;
    }

    public Optional<Recommendation> findById(Long id) {
        return recommendationRepository.findById(id);
    }


    public Recommendation createRecommendation(RecommendationDTO recommendationDTO) throws ResourceNotFoundException, DuplicateResourceException {
        Long userId = getUserIdByUsername(recommendationDTO.getUsername());
        Long movieId = getMovieIdByTitle(recommendationDTO.getMovieTitle());

        if (recommendationRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new DuplicateResourceException("Recommendation for user '" + recommendationDTO.getUsername() + "' and movie '" + recommendationDTO.getMovieTitle() + "' already exists.");
        }

        Recommendation recommendation = new Recommendation();
        recommendation.setUserId(userId);
        recommendation.setMovieId(movieId);
        recommendation.setRecommendationScore(recommendationDTO.getRecommendationScore());
        recommendation.setIsViewed(recommendationDTO.getIsViewed());
        return recommendationRepository.save(recommendation);
    }


    public Recommendation updateRecommendation(Long id, RecommendationDTO recommendationDTO) throws ResourceNotFoundException {
        Optional<Recommendation> existingRecommendationOpt = recommendationRepository.findById(id);
        if (existingRecommendationOpt.isEmpty()) {
            throw new ResourceNotFoundException("Recommendation with id " + id + " not found");
        }
        Recommendation existingRecommendation = existingRecommendationOpt.get();

        existingRecommendation.setRecommendationScore(recommendationDTO.getRecommendationScore());
        existingRecommendation.setIsViewed(recommendationDTO.getIsViewed());
        return recommendationRepository.save(existingRecommendation);
    }


    public void deleteById(Long id) throws ResourceNotFoundException {
        if (!recommendationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recommendation with id " + id + " not found");
        }
        recommendationRepository.deleteById(id);
    }

    public RecommendationDTO toDTO(Recommendation recommendation) {
        String username;
        try {
            ResponseEntity<UserDTO> userResponse = restTemplate.getForEntity(userServiceUrl + "/users/" + recommendation.getUserId(), UserDTO.class);
            username = userResponse.getStatusCode().is2xxSuccessful() && userResponse.getBody() != null ? userResponse.getBody().getUsername() : "Unknown User";
        } catch (Exception e) {
            System.err.println("Error fetching username for user id " + recommendation.getUserId() + ": " + e.getMessage());
            username = "Error Fetching User";
        }

        String movieTitle;
        try {
            ResponseEntity<MovieDTO> movieResponse = restTemplate.getForEntity(movieServiceUrl + "/movies/" + recommendation.getMovieId(), MovieDTO.class);
            movieTitle = movieResponse.getStatusCode().is2xxSuccessful() && movieResponse.getBody() != null ? movieResponse.getBody().getTitle() : "Unknown Movie";
        } catch (Exception e) {
            System.err.println("Error fetching movie title for movie id " + recommendation.getMovieId() + ": " + e.getMessage());
            movieTitle = "Error Fetching Movie";
        }

        RecommendationDTO dto = new RecommendationDTO();
        dto.setRecommendationId(recommendation.getRecommendationId());
        dto.setUsername(username);
        dto.setMovieTitle(movieTitle);
        dto.setRecommendationScore(recommendation.getRecommendationScore());
        dto.setIsViewed(recommendation.getIsViewed());

        return dto;
    }

    public List<RecommendationDTO> toDTOList(List<Recommendation> recommendations) {
        return recommendations.stream()
                .map(this::toDTO)
                .toList();
    }
}
