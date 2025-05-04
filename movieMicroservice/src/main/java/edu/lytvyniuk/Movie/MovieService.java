package edu.lytvyniuk.Movie;

/*
  @author darin
  @project microservices
  @class MovieService
  @version 1.0.0
  @since 28.04.2025 - 13.42
*/

import edu.lytvyniuk.DTOs.GenreDTO;
import edu.lytvyniuk.DTOs.RatingDTO;
import edu.lytvyniuk.Movie.Genre.Genre;
import edu.lytvyniuk.Movie.Genre.GenreService;
import edu.lytvyniuk.Movie.MovieGenre.MovieGenre;
import edu.lytvyniuk.Movie.MovieGenre.MovieGenreService;
import edu.lytvyniuk.customException.DuplicateResourceException;
import edu.lytvyniuk.customException.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final GenreService genreService;
    private final RestTemplate restTemplate;
    private final MovieGenreService movieGenreService;

    @Value("${ratings.service.url}")
    private String ratingsServiceUrl;


    public MovieService(MovieRepository movieRepository, GenreService genreService, RestTemplate restTemplate, MovieGenreService movieGenreService) {
        this.movieRepository = movieRepository;
        this.genreService = genreService;
        this.restTemplate = restTemplate;
        this.movieGenreService = movieGenreService;
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

    public Movie save(Movie movie, List<GenreDTO> genres) throws DuplicateResourceException {
        if (movieRepository.existsByTitleAndReleaseDate(movie.getTitle(), movie.getReleaseDate())) {
            throw new DuplicateResourceException("Movie with title '" + movie.getTitle() +
                    "' and release date '" + movie.getReleaseDate() + "' already exists");
        }

        Movie savedMovie = movieRepository.save(movie);
        movieGenreService.saveAllMovieGenres(savedMovie, genres);

        return savedMovie;
    }

    public Movie updateMovie(Long id, Movie movieDetails) throws ResourceNotFoundException {
        Optional<Movie> existingMovieOpt = movieRepository.findById(id);
        if (existingMovieOpt.isPresent()) {
            Movie existingMovie = existingMovieOpt.get();
            existingMovie.setTitle(movieDetails.getTitle());
            existingMovie.setDescription(movieDetails.getDescription());
            existingMovie.setReleaseDate(movieDetails.getReleaseDate());
            existingMovie.setDuration(movieDetails.getDuration());
            existingMovie.setAverageRating(movieDetails.getAverageRating());


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

    public List<RatingDTO> getRatingsForMovie(Long movieId) {
        String url = ratingsServiceUrl + "/ratings/movie/" + movieId;
        try {
            ResponseEntity<List<RatingDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<RatingDTO>>() {}
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return List.of();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching ratings for movie " + movieId + ": " + e.getMessage(), e);
        }
    }

    public Map<Long, String> getMovieTitlesByIds(List<Long> ids) {
        List<Object[]> results = movieRepository.findMovieIdAndTitleByIdIn(ids);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],    // movieId
                        row -> (String) row[1]   // title
                ));
    }
}