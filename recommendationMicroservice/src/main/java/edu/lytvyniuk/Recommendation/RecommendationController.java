package edu.lytvyniuk.Recommendation;

/*
  @author darin
  @project microservices
  @class RecommendationController
  @version 1.0.0
  @since 28.04.2025 - 14.32
*/

import edu.lytvyniuk.DTOs.RecommendationDTO;
import edu.lytvyniuk.customException.DuplicateResourceException;
import edu.lytvyniuk.customException.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationMapper recommendationMapper;

    public RecommendationController(RecommendationService recommendationService, RecommendationMapper recommendationMapper) {
        this.recommendationService = recommendationService;
        this.recommendationMapper = recommendationMapper;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecommendationDTO>> getRecommendationsByUserId(@PathVariable(name = "userId") Long userId) throws ResourceNotFoundException {
        List<Recommendation> recommendations = recommendationService.findByUserId(userId);

        List<RecommendationDTO> responseDTOs = recommendationService.toDTOList(recommendations);

        if (responseDTOs.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecommendationDTO> getRecommendationById(@PathVariable(name = "id") Long id) {
        Optional<Recommendation> recommendationOpt = recommendationService.findById(id);
        if (recommendationOpt.isPresent()) {
            Recommendation recommendation = recommendationOpt.get();
            // Використовуємо сервіс для перетворення в DTO
            RecommendationDTO recommendationDTO = recommendationService.toDTO(recommendation); // Або recommendationMapper.toDTO(recommendation);
            return ResponseEntity.ok(recommendationDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Повертаємо 201 Created
    public ResponseEntity<RecommendationDTO> createRecommendation(@Valid @RequestBody RecommendationDTO recommendationDTO) throws ResourceNotFoundException, DuplicateResourceException {
        // Сервіс приймає DTO, отримує з нього username і movieTitle, знаходить ID і зберігає.
        Recommendation createdRecommendation = recommendationService.createRecommendation(recommendationDTO);

        // Використовуємо сервіс для перетворення збереженої Entity назад в DTO для відповіді
        RecommendationDTO createdRecommendationDTO = recommendationService.toDTO(createdRecommendation); // Або recommendationMapper.toDTO(createdRecommendation);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecommendationDTO);
    }

    // Приклад ендпоінту для оновлення рекомендації (наприклад, відзначити як переглянуту)
    @PutMapping("/{id}")
    public ResponseEntity<RecommendationDTO> updateRecommendation(@PathVariable(name = "id") Long id, @Valid @RequestBody RecommendationDTO recommendationDTO) throws ResourceNotFoundException {
        // Сервіс оновлює рекомендацію за ID, використовуючи дані з DTO
        Recommendation updatedRecommendation = recommendationService.updateRecommendation(id, recommendationDTO);

        // Використовуємо сервіс для перетворення оновленої Entity в DTO для відповіді
        RecommendationDTO updatedRecommendationDTO = recommendationService.toDTO(updatedRecommendation); // Або recommendationMapper.toDTO(updatedRecommendation);

        return ResponseEntity.ok(updatedRecommendationDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendation(@PathVariable(name = "id") Long id) throws ResourceNotFoundException {
        recommendationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
