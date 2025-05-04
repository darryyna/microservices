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
        List<RatingDTO> ratingDTOs = ratingService.findAllDTOs();
        return ResponseEntity.ok(ratingDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingDTO> getRatingById(@PathVariable (name = "id") Long id) {
        Optional<Rating> ratingOpt = ratingService.findById(id);
        if (ratingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RatingDTO dto = ratingService.mapAndEnrichRatingDTO(ratingOpt.get());
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<RatingDTO>> getRatingsByMovieId(@PathVariable(name = "movieId") Long movieId) throws ResourceNotFoundException {
        List<RatingDTO> ratingDTOs = ratingService.findRatingDTOsByMovieId(movieId);
        return ResponseEntity.ok(ratingDTOs);
    }

    @PostMapping
    public ResponseEntity<RatingDTO> createRating(@Valid @RequestBody RatingDTO ratingDTO) throws DuplicateResourceException, ResourceNotFoundException {
        Rating rating = ratingMapper.toEntity(ratingDTO);

        RatingDTO createdRatingDTO = ratingService.createRating(rating, ratingDTO.getUsername(), ratingDTO.getMovieTitle());

        return new ResponseEntity<>(createdRatingDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable(name = "id") Long id) throws ResourceNotFoundException {
        ratingService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}