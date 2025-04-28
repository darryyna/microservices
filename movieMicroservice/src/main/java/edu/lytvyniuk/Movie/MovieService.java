package edu.lytvyniuk.Movie;

/*
  @author darin
  @project microservices
  @class MovieService
  @version 1.0.0
  @since 28.04.2025 - 13.42
*/

import edu.lytvyniuk.DTOs.RatingDTO;
import edu.lytvyniuk.customException.DuplicateResourceException;
import edu.lytvyniuk.customException.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Важливо для управління транзакціями
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException; // Для обробки 404 тощо

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final RestTemplate restTemplate; // Для викликів до інших сервісів

    @Value("http://localhost:9993") // URL rating-microservice
    private String ratingsServiceUrl;

    // Припустимо, MovieGenreService залишається тут
    // private final MovieGenreService movieGenreService;

    public MovieService(MovieRepository movieRepository, RestTemplate restTemplate /*, MovieGenreService movieGenreService */) {
        this.movieRepository = movieRepository;
        this.restTemplate = restTemplate;
        // this.movieGenreService = movieGenreService;
    }

    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    public Optional<Movie> findById(Long id) {
        return movieRepository.findById(id);
    }


    public List<Movie> findByTitle(String title) throws ResourceNotFoundException {
        List<Movie> movies = movieRepository.findByTitle(title);
        if(movies.isEmpty()) {
            throw new ResourceNotFoundException("Movie with title " + title + " not found");
        }
        return movies;
    }

    public Optional<Movie> findMovieByTitle(String title) throws ResourceNotFoundException {
        Optional<Movie> movie = movieRepository.findMovieByTitle(title);
        if(movie.isEmpty()) {
            throw new ResourceNotFoundException("Movie with title " + title + " not found");
        }
        return movie;
    }


    public List<Movie> findByMinimumRating(Double minRating) throws ResourceNotFoundException {
        List<Movie> movies = movieRepository.findByMinimumRating(minRating);
        if(movies.isEmpty()) {
            throw new ResourceNotFoundException("Movie with minimum rating " + minRating + " not found");
        }
        return movies;
    }

    public Movie save(Movie movie) throws DuplicateResourceException {
        // Перевірка на дублікат тепер простіша завдяки новому методу в репозиторії
        if (movieRepository.existsByTitleAndReleaseDate(movie.getTitle(), movie.getReleaseDate())) {
            throw new DuplicateResourceException("Movie with title '" + movie.getTitle() +
                    "' and release date '" + movie.getReleaseDate() + "' already exists");
        }
        // Тут також потрібно врахувати збереження movieGenres, якщо вони прийшли з MovieDTO
        // Це залежить від того, як ви мапите DTO -> Entity, і чи MovieMapper тут використовується
        // або логіка перенесення жанрів в Entity відбувається тут.
        return movieRepository.save(movie);
    }

    public Movie updateMovie(Long id, Movie movieDetails) throws ResourceNotFoundException {
        Optional<Movie> existingMovieOpt = movieRepository.findById(id);
        if (existingMovieOpt.isPresent()) {
            Movie existingMovie = existingMovieOpt.get();
            // Оновлюємо поля існуючого фільму з movieDetails
            existingMovie.setTitle(movieDetails.getTitle());
            existingMovie.setDescription(movieDetails.getDescription());
            existingMovie.setReleaseDate(movieDetails.getReleaseDate());
            existingMovie.setDuration(movieDetails.getDuration());
            existingMovie.setAverageRating(movieDetails.getAverageRating()); // Оновлення averageRating

            // Логіка оновлення movieGenres, якщо вони керуються цим сервісом
            // Це може бути складно і потребує очищення існуючих зв'язків та додавання нових.
            // existingMovie.getMovieGenres().clear(); // Очищаємо старі
            // movieDetails.getMovieGenres().forEach(existingMovie::addMovieGenre); // Додаємо нові (припускаючи, що movieDetails має Set<MovieGenre>)


            return movieRepository.save(existingMovie);
        } else {
            throw new ResourceNotFoundException("Movie with id " + id + " not found");
        }
    }

    public void deleteById(Long id) throws ResourceNotFoundException {
        if (!movieRepository.existsById(id)) {
            throw new ResourceNotFoundException("Movie with id " + id + " not found");
        }
        movieRepository.deleteById(id);
    }

    // Приклад методу для отримання рейтингів фільму з rating-microservice
    public List<RatingDTO> getRatingsForMovie(Long movieId) {
        String url = ratingsServiceUrl + "/ratings/movie/" + movieId; // Ендпоінт в rating-service
        try {
            // Використовуємо ParameterizedTypeReference для списку DTO
            ResponseEntity<List<RatingDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<RatingDTO>>() {}
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            // Якщо рейтинги для цього фільму не знайдені (це нормально, може бути пустий список)
            return List.of(); // Повертаємо пустий список
        } catch (Exception e) {
            // Обробка інших помилок зв'язку
            // Можливо, варто кинути власне виключення або логувати
            throw new RuntimeException("Error fetching ratings for movie " + movieId + ": " + e.getMessage(), e);
        }
    }
}