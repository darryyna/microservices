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
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
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

    private String getMovieTitleByMovieId(Long movieId) {
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

    private RatingDTO mapAndEnrichRatingDTO(Rating rating) {
        RatingDTO ratingDTO = ratingMapper.toDTO(rating);
        ratingDTO.setUsername(getUsernameByUserId(rating.getUserId()));
        ratingDTO.setMovieTitle(getMovieTitleByMovieId(rating.getMovieId()));

        return ratingDTO;
    }


    public List<RatingDTO> findAllDTOs() {
        List<Rating> ratings = ratingRepository.findAll();
        return ratings.stream()
                .map(this::mapAndEnrichRatingDTO)
                .collect(Collectors.toList());
    }

    public Optional<RatingDTO> findDTOById(Long id) {
        Optional<Rating> ratingOptional = ratingRepository.findById(id);
        if (ratingOptional.isPresent()) {
                       return Optional.of(mapAndEnrichRatingDTO(ratingOptional.get()));
        } else {
            return Optional.empty();
        }
    }

    public List<RatingDTO> findRatingDTOsByMovieId(Long movieId) throws ResourceNotFoundException {
        List<Rating> ratings = ratingRepository.findByMovieId(movieId);
        return ratings.stream()
                .map(this::mapAndEnrichRatingDTO)
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


    private Optional<Rating> findById(Long id) {
        return ratingRepository.findById(id);
    }

    private List<Rating> findRatingsByMovieId(Long movieId) {
        return ratingRepository.findByMovieId(movieId);
    }
}
