package edu.lytvyniuk.Rating;

/*
  @author darin
  @project microservices
  @class RatingService
  @version 1.0.0
  @since 28.04.2025 - 13.12
*/

import edu.lytvyniuk.DTOs.MovieDTO;
import edu.lytvyniuk.DTOs.RatingDTO;
import edu.lytvyniuk.DTOs.UserDTO;
import edu.lytvyniuk.customException.DuplicateResourceException;
import edu.lytvyniuk.customException.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final RestTemplate restTemplate;
    private final RatingMapper ratingMapper;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${movie.service.url}")
    private String movieServiceUrl;

    public RatingService(RatingRepository ratingRepository, RestTemplate restTemplate, RatingMapper ratingMapper) {
        this.ratingRepository = ratingRepository;
        this.restTemplate = restTemplate;
        this.ratingMapper = ratingMapper;
    }

    public List<Rating> findAll() {
        return ratingRepository.findAll();
    }

    private Long getUserIdByUsername(String username) throws ResourceNotFoundException {
        String url = userServiceUrl + "/users/username/" + username;
        try {
            try {
                UserDTO userDTO = restTemplate.getForObject(url, UserDTO.class);
                if (userDTO != null && userDTO.getUserId() != null) {
                    return userDTO.getUserId();
                } else {
                    throw new ResourceNotFoundException("User ID not found for username: " + username);
                }
            } catch (HttpClientErrorException.NotFound e) {
                throw new ResourceNotFoundException("User not found with username: " + username);
            }


        } catch (Exception e) {
            throw new RuntimeException("Error calling user service: " + e.getMessage(), e);
        }
    }

    private Long getMovieIdByTitle(String movieTitle) throws ResourceNotFoundException {
        String url = movieServiceUrl + "/movies/title/" + movieTitle;
        try {
            try {
                MovieDTO movieDTO = restTemplate.getForObject(url, MovieDTO.class);
                if (movieDTO != null && movieDTO.getMovieId() != null) {
                    return movieDTO.getMovieId();
                } else {
                    throw new ResourceNotFoundException("Movie ID not found for title: " + movieTitle);
                }
            } catch (HttpClientErrorException.NotFound e) {
                throw new ResourceNotFoundException("Movie not found with title: " + movieTitle);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error calling movie service: " + e.getMessage(), e);
        }
    }

    private String getUsernameByUserId(Long userId) {
        String url = userServiceUrl + "/users/" + userId;
        try {
            UserDTO userDTO = restTemplate.getForObject(url, UserDTO.class);
            return (userDTO != null && userDTO.getUsername() != null) ? userDTO.getUsername() : "Unknown User";
        } catch (HttpClientErrorException.NotFound e) {
            return "Unknown User";
        } catch (Exception e) {
            return "Unknown User";
        }
    }

    public String getMovieTitleByMovieId(Long movieId) {
        String url = movieServiceUrl + "/movies/" + movieId;
        try {
            MovieDTO movieDTO = restTemplate.getForObject(url, MovieDTO.class);
            return (movieDTO != null && movieDTO.getTitle() != null) ? movieDTO.getTitle() : "Unknown Movie";
        } catch (HttpClientErrorException.NotFound e) {
            return "Unknown Movie";
        } catch (Exception e) {
            return "Unknown Movie";
        }
    }

    private Map<Long, String> getMovieTitlesByIds(List<Long> movieIds) {
        String url = movieServiceUrl + "/movies/titles?ids=" +
                movieIds.stream()
                        .distinct()
                        .map(String::valueOf)
                        .collect(Collectors.joining("&ids="));  // змінилося з коми на окремі параметри

        try {
            ParameterizedTypeReference<Map<Long, String>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<Map<Long, String>> response = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch movie titles in batch", e);
        }
    }


    public RatingDTO mapAndEnrichRatingDTO(Rating rating) {
        RatingDTO ratingDTO = ratingMapper.toDTO(rating);
        ratingDTO.setUsername(getUsernameByUserId(rating.getUserId()));
        ratingDTO.setMovieTitle(getMovieTitleByMovieId(rating.getMovieId()));
        return ratingDTO;
    }


    public List<RatingDTO> findAllDTOs() {
        List<Rating> ratings = ratingRepository.findAll();

        Map<Long, String> movieTitles = getMovieTitlesByIds(
                ratings.stream().map(Rating::getMovieId).distinct().collect(Collectors.toList())
        );

        return ratings.stream()
                .map(rating -> {
                    RatingDTO dto = ratingMapper.toDTO(rating);
                    dto.setUsername(getUsernameByUserId(rating.getUserId()));
                    dto.setMovieTitle(movieTitles.getOrDefault(rating.getMovieId(), "Unknown Movie"));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<RatingDTO> findRatingDTOsByMovieId(Long movieId) throws ResourceNotFoundException {
        List<Rating> ratings = ratingRepository.findByMovieId(movieId); // або findAll()
        Map<Long, String> movieTitles = getMovieTitlesByIds(
                ratings.stream().map(Rating::getMovieId).collect(Collectors.toList())
        );

        return ratings.stream()
                .map(rating -> {
                    RatingDTO dto = ratingMapper.toDTO(rating);
                    dto.setUsername(getUsernameByUserId(rating.getUserId()));
                    dto.setMovieTitle(movieTitles.getOrDefault(rating.getMovieId(), "Unknown Movie"));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public RatingDTO createRating(Rating rating, String username, String movieTitle) throws DuplicateResourceException, ResourceNotFoundException {
        Long userId = getUserIdByUsername(username);
        Long movieId = getMovieIdByTitle(movieTitle);

        if (ratingRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new DuplicateResourceException("User has already rated this movie.");
        }
        rating.setUserId(userId);
        rating.setMovieId(movieId);

        Rating savedRating = ratingRepository.save(rating);

        return mapAndEnrichRatingDTO(savedRating);
    }

    public void deleteById(Long id) throws ResourceNotFoundException {
        if (!ratingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rating with id " + id + " not found");
        }
        ratingRepository.deleteById(id);
    }


    public Optional<Rating> findById(Long id) {
        return ratingRepository.findById(id);
    }
}
