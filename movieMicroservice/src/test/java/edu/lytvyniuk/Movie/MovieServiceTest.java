package edu.lytvyniuk.Movie;

import edu.lytvyniuk.Movie.Genre.GenreService;
import edu.lytvyniuk.Movie.MovieGenre.MovieGenreService;
import edu.lytvyniuk.customException.DuplicateResourceException;
import edu.lytvyniuk.customException.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private GenreService genreService;

    @Mock
    private MovieGenreService movieGenreService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MovieService movieService;

    private Movie movie;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setMovieId(1L);
        movie.setTitle("Inception");
        movie.setDescription("A mind-bending thriller");
        movie.setReleaseDate(LocalDate.of(2010, 7, 16));
        movie.setDuration(148);
        movie.setAverageRating(8.8);
    }

    @Test
    void findAll_shouldReturnListOfMovies() {
        when(movieRepository.findAll()).thenReturn(Collections.singletonList(movie));

        List<Movie> movies = movieService.findAll();

        assertFalse(movies.isEmpty());
        assertEquals(1, movies.size());
        assertEquals("Inception", movies.get(0).getTitle());
    }

    @Test
    void findById_whenMovieExists_shouldReturnMovie() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        Optional<Movie> foundMovie = movieService.findById(1L);

        assertTrue(foundMovie.isPresent());
        assertEquals("Inception", foundMovie.get().getTitle());
    }

    @Test
    void findByTitle_whenMoviesExist_shouldReturnList() throws ResourceNotFoundException {
        when(movieRepository.findByTitle("Inception")).thenReturn(Collections.singletonList(movie));

        List<Movie> movies = movieService.findByTitle("Inception");

        assertFalse(movies.isEmpty());
        assertEquals(1, movies.size());
    }

    @Test
    void findByTitle_whenNoMoviesExist_shouldThrowException() {
        when(movieRepository.findByTitle("Unknown")).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> movieService.findByTitle("Unknown"));
    }

    @Test
    void save_whenMovieIsValid_shouldSaveMovie() throws DuplicateResourceException {
        when(movieRepository.existsByTitleAndReleaseDate(any(), any())).thenReturn(false);
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        Movie savedMovie = movieService.save(movie, new ArrayList<>());

        assertNotNull(savedMovie);
        assertEquals("Inception", savedMovie.getTitle());
        verify(movieRepository, times(1)).save(movie);
        verify(movieGenreService, times(1)).saveAllMovieGenres(any(), any());
    }

    @Test
    void save_whenMovieAlreadyExists_shouldThrowException() {
        when(movieRepository.existsByTitleAndReleaseDate(any(), any())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> movieService.save(movie, new ArrayList<>()));
    }

    @Test
    void deleteById_whenMovieExists_shouldDelete() throws ResourceNotFoundException {
        when(movieRepository.existsById(1L)).thenReturn(true);

        movieService.deleteById(1L);

        verify(movieRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_whenMovieDoesNotExist_shouldThrowException() {
        when(movieRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> movieService.deleteById(1L));
    }
}
