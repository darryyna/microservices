package edu.lytvyniuk.Recommendation;

import edu.lytvyniuk.customException.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RecommendationService recommendationService;

    private Recommendation recommendation;

    @BeforeEach
    void setUp() {
        recommendation = new Recommendation();
        recommendation.setRecommendationId(1L);
        recommendation.setUserId(1L);
        recommendation.setMovieId(1L);
        recommendation.setRecommendationScore(9.0);
        recommendation.setIsViewed(false);
    }

    @Test
    void findByUserId_whenRecommendationsExist_shouldReturnList() throws ResourceNotFoundException {
        when(recommendationRepository.findByUserId(1L)).thenReturn(Collections.singletonList(recommendation));

        List<Recommendation> recommendations = recommendationService.findByUserId(1L);

        assertFalse(recommendations.isEmpty());
        assertEquals(1, recommendations.size());
        assertEquals(9.0, recommendations.get(0).getRecommendationScore());
    }

    @Test
    void findByUserId_whenNoRecommendationsExist_shouldThrowException() {
        when(recommendationRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> recommendationService.findByUserId(1L));
    }

    @Test
    void findById_whenExists_shouldReturnRecommendation() {
        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendation));

        Optional<Recommendation> found = recommendationService.findById(1L);

        assertTrue(found.isPresent());
        assertEquals(9.0, found.get().getRecommendationScore());
    }

    @Test
    void deleteById_whenExists_shouldDelete() throws ResourceNotFoundException {
        when(recommendationRepository.existsById(1L)).thenReturn(true);

        recommendationService.deleteById(1L);

        verify(recommendationRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_whenDoesNotExist_shouldThrowException() {
        when(recommendationRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> recommendationService.deleteById(1L));
    }
}
