package edu.lytvyniuk.Rating;

/*
  @author darin
  @project microservices
  @class RatingService
  @version 1.0.0
  @since 28.04.2025 - 13.12
*/

import edu.lytvyniuk.DTOs.MovieDTO;
import edu.lytvyniuk.DTOs.UserDTO;
import edu.lytvyniuk.customException.DuplicateResourceException;
import edu.lytvyniuk.customException.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final RestTemplate restTemplate;

    @Value("http://localhost:9991")
    private String userServiceUrl;

    @Value("http://localhost:9992")
    private String movieServiceUrl;

    public RatingService(RatingRepository ratingRepository, RestTemplate restTemplate) {
        this.ratingRepository = ratingRepository;
        this.restTemplate = restTemplate;
    }

    public List<Rating> findAll() {
        return ratingRepository.findAll();
    }

    // Метод для отримання User ID за username з user-microservice
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

    public Rating createRating(Rating rating, String username, String movieTitle) throws DuplicateResourceException, ResourceNotFoundException {
        Long userId = getUserIdByUsername(username);
        Long movieId = getMovieIdByTitle(movieTitle);

        if (ratingRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new DuplicateResourceException("User has already rated this movie.");
        }

        rating.setUserId(userId);
        rating.setMovieId(movieId);

        return ratingRepository.save(rating);
    }

    public List<Rating> findRatingsByMovieId(Long movieId) throws ResourceNotFoundException {

        return ratingRepository.findByMovieId(movieId);
    }

    public Optional<Rating> findById(Long id) {
        return ratingRepository.findById(id);
    }

    public void deleteById(Long id) throws ResourceNotFoundException {
        if (!ratingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rating with id " + id + " not found");
        }
        ratingRepository.deleteById(id);
    }
}
