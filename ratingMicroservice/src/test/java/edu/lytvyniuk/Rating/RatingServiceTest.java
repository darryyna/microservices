package edu.lytvyniuk.Rating;

import edu.lytvyniuk.DTOs.RatingDTO;
import edu.lytvyniuk.customException.DuplicateResourceException;
import edu.lytvyniuk.customException.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RatingMapper ratingMapper;

    @InjectMocks
    private RatingService ratingService;

    private Rating rating;

    @BeforeEach
    void setUp() {
        rating = new Rating();
        rating.setRatingId(1L);
        rating.setScore(8.5);
        rating.setComment("Great movie!");
        rating.setRatingDate(LocalDateTime.now());
        rating.setUserId(1L);
        rating.setMovieId(1L);
    }

    @Test
    void findAll_shouldReturnListOfRatings() {
        when(ratingRepository.findAll()).thenReturn(Collections.singletonList(rating));

        List<Rating> ratings = ratingService.findAll();

        assertFalse(ratings.isEmpty());
        assertEquals(1, ratings.size());
        assertEquals(1L, ratings.get(0).getUserId());
    }

    @Test
    void findById_whenRatingExists_shouldReturnRating() {
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));

        Optional<Rating> foundRating = ratingService.findById(1L);

        assertTrue(foundRating.isPresent());
        assertEquals(8.5, foundRating.get().getScore());
    }

    @Test
    void deleteById_whenRatingExists_shouldDelete() throws ResourceNotFoundException {
        when(ratingRepository.existsById(1L)).thenReturn(true);

        ratingService.deleteById(1L);

        verify(ratingRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_whenRatingDoesNotExist_shouldThrowException() {
        when(ratingRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> ratingService.deleteById(1L));
    }
}
