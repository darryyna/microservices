package edu.lytvyniuk.Rating;

/*
  @author darin
  @project microservices
  @class RatingController
  @version 1.0.0
  @since 28.04.2025 - 13.12
*/

import edu.lytvyniuk.DTOs.RatingDTO;
import edu.lytvyniuk.customException.DuplicateResourceException;
import edu.lytvyniuk.customException.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ratings")
public class RatingController {
    private final RatingService ratingService;
    private final RatingMapper ratingMapper;

    public RatingController(RatingService ratingService, RatingMapper ratingMapper) {
        this.ratingService = ratingService;
        this.ratingMapper = ratingMapper;
    }

    @GetMapping
    public ResponseEntity<List<RatingDTO>> getAllRatings() {
        List<Rating> ratings = ratingService.findAll();
        // При маппінгу Entity -> DTO поля username та movieTitle не заповнюються
        // Якщо потрібно, їх потрібно отримати додатково в сервісі або тут.
        // Наприклад, можна створити інший ендпоінт, який повертає RatingDetailsDTO
        // який включає ім'я користувача та назву фільму.
        List<RatingDTO> ratingDTOs = ratings.stream()
                .map(ratingMapper::toDTO) // Тут username та movieTitle будуть null
                .collect(Collectors.toList());
        return ResponseEntity.ok(ratingDTOs);
    }

    @GetMapping("/{id}") // Додано ендпоінт для отримання рейтингу за ID
    public ResponseEntity<RatingDTO> getRatingById(@PathVariable(name = "id") Long id) {
        Optional<Rating> rating = ratingService.findById(id);
        if (rating.isPresent()) {
            RatingDTO ratingDTO = ratingMapper.toDTO(rating.get());
            // !!! Якщо потрібно username та movieTitle тут, їх треба отримати окремо
            // викликаючи user-service та movie-service за rating.getUserId() та rating.getMovieId()
            return ResponseEntity.ok(ratingDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/movie/{movieId}") // Додано ендпоінт для отримання рейтингів за ID фільму
    public ResponseEntity<List<RatingDTO>> getRatingsByMovieId(@PathVariable(name = "movieId") Long movieId) throws ResourceNotFoundException {
        List<Rating> ratings = ratingService.findRatingsByMovieId(movieId);
        List<RatingDTO> ratingDTOs = ratings.stream()
                .map(ratingMapper::toDTO) // username та movieTitle не заповнені
                .collect(Collectors.toList());
        return ResponseEntity.ok(ratingDTOs);
    }


    @PostMapping
    public ResponseEntity<RatingDTO> createRating(@Valid @RequestBody RatingDTO ratingDTO) throws DuplicateResourceException, ResourceNotFoundException {
        Rating rating = ratingMapper.toEntity(ratingDTO); // score, comment, ratingDate мапаються

        // Сервіс відповідає за отримання userId та movieId та збереження
        Rating createdRating = ratingService.createRating(rating, ratingDTO.getUsername(), ratingDTO.getMovieTitle());

        RatingDTO createdRatingDTO = ratingMapper.toDTO(createdRating);
        // createdRatingDTO буде мати null в username та movieTitle, якщо ви їх не отримаєте і не встановите тут
        return new ResponseEntity<>(createdRatingDTO, HttpStatus.CREATED);
    }

    // Додайте ендпоінт для видалення, якщо потрібно
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable(name = "id") Long id) throws ResourceNotFoundException {
        ratingService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}