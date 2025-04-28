package edu.lytvyniuk.Movie;

/*
  @author darin
  @project microservices
  @class MovieController
  @version 1.0.0
  @since 28.04.2025 - 13.42
*/

import edu.lytvyniuk.DTOs.MovieDTO;
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
@RequestMapping("/movies")
public class MovieController {
    private final MovieService movieService;
    // private final MovieGenreService movieGenreService; // Видалено
    // private final RatingService ratingService; // Видалено
    private final MovieMapper movieMapper;

    // Конструктор оновлено без MovieGenreService та RatingService
    public MovieController(MovieService movieService, MovieMapper movieMapper /*, MovieGenreService movieGenreService */) {
        this.movieService = movieService;
        this.movieMapper = movieMapper;
        // this.movieGenreService = movieGenreService;
    }

    // Допоміжний метод для отримання MovieDTO з можливістю отримати рейтинги та жанри
    // Цей метод ілюструє, як зібрати повне DTO, викликаючи інші сервіси.
    // В реальному проекті така логіка може бути в Aggregation Service або Gateway.
    private MovieDTO getMovieDTOWithDetails(Movie movie) {
        MovieDTO movieDTO = movieMapper.toDTO(movie);

        // Якщо потрібно включити жанри, викликаємо MovieGenreService (якщо він тут)
        // або отримуємо їх з movie.getMovieGenres() якщо зв'язок залишився
        // movieDTO.setGenres(movieMapper.movieGenresToDTOs(movie.getMovieGenres()));


        // Якщо потрібно включити рейтинги, викликаємо RatingService через MovieService
        try {
            List<RatingDTO> ratings = movieService.getRatingsForMovie(movie.getMovieId());
            // Можливо, потрібно створити MovieDetailsDTO, який включає List<RatingDTO>
            // Для простоти, припустимо, що ми не додаємо рейтинги назад в MovieDTO тут.
            // Якщо потрібно, створіть новий DTO (наприклад, MovieDetailsDTO)
            // і повертайте його.
            System.out.println("Fetched " + ratings.size() + " ratings for movie " + movie.getMovieId());

        } catch (RuntimeException e) {
            // Обробка помилок при отриманні рейтингів (наприклад, логування)
            System.err.println("Error fetching ratings for movie " + movie.getMovieId() + ": " + e.getMessage());
            // Можливо, варто встановити ratings в null або порожній список у DTO
        }

        return movieDTO; // Повертаємо MovieDTO без вбудованих рейтингів
    }


    @GetMapping
    public ResponseEntity<List<MovieDTO>> getAllMovies() {
        List<Movie> movies = movieService.findAll();

        // Перетворюємо список Entity в список DTO
        List<MovieDTO> movieDTOs = movies.stream()
                .map(this::getMovieDTOWithDetails) // Використовуємо допоміжний метод
                .collect(Collectors.toList());

        return ResponseEntity.ok(movieDTOs);
    }

    @GetMapping("/{id}") // Додано ендпоінт для отримання фільму за ID
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable(name = "id") Long id) throws ResourceNotFoundException {
        Optional<Movie> movieOpt = movieService.findById(id);
        if (movieOpt.isPresent()) {
            Movie movie = movieOpt.get();
            MovieDTO movieDTO = getMovieDTOWithDetails(movie); // Отримуємо DTO з деталями
            return ResponseEntity.ok(movieDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/title/{title}") // Змінено шлях для уникнення конфлікту з /{id}
    public ResponseEntity<List<MovieDTO>> getMoviesByTitle(@PathVariable(name = "title") String title) throws ResourceNotFoundException {
        List<Movie> movies = movieService.findByTitle(title);

        List<MovieDTO> movieDTOs = movies.stream()
                .map(this::getMovieDTOWithDetails)
                .collect(Collectors.toList());

        return ResponseEntity.ok(movieDTOs);
    }

    @GetMapping("/rating/{minRating}")
    public ResponseEntity<List<MovieDTO>> getMoviesByMinimumRating(@PathVariable(name = "minRating") Double minRating) throws ResourceNotFoundException {
        List<Movie> movies = movieService.findByMinimumRating(minRating);

        List<MovieDTO> movieDTOs = movies.stream()
                .map(this::getMovieDTOWithDetails)
                .collect(Collectors.toList());

        return ResponseEntity.ok(movieDTOs);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Повертаємо 201 Created при успішному створенні
    public ResponseEntity<MovieDTO> createMovie(@Valid @RequestBody MovieDTO movieDTO) throws DuplicateResourceException {
        Movie movie = movieMapper.toEntity(movieDTO); // Мапимо DTO в Entity
        Movie createdMovie = movieService.save(movie);

        // Можливо, потрібно оновити averageRating після створення? (Якщо додається початковий рейтинг)
        // movieService.updateAverageRating(createdMovie.getMovieId());


        MovieDTO createdMovieDTO = movieMapper.toDTO(createdMovie); // Мапимо назад в DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovieDTO);
    }


    @PutMapping("/{id}")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable(name = "id") Long id, @Valid @RequestBody MovieDTO movieDTO) throws ResourceNotFoundException {
        // Знаходимо існуючий фільм
        Optional<Movie> existingMovieOpt = movieService.findById(id);
        if (existingMovieOpt.isEmpty()) {
            throw new ResourceNotFoundException("Movie with id " + id + " not found");
        }
        Movie existingMovie = existingMovieOpt.get();

        // Оновлюємо існуючий об'єкт Movie з даних DTO за допомогою маппера
        movieMapper.updateEntityFromDTO(movieDTO, existingMovie);

        // Зберігаємо оновлений фільм
        Movie updatedMovie = movieService.updateMovie(id, existingMovie);

        // Можливо, averageRating змінився, варто його оновити?
        // movieService.updateAverageRating(updatedMovie.getMovieId());


        // Повертаємо оновлений DTO
        MovieDTO updatedMovieDTO = movieMapper.toDTO(updatedMovie);
        return ResponseEntity.ok(updatedMovieDTO);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable(name = "id") Long id) throws ResourceNotFoundException {
        movieService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}