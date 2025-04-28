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
    private final MovieMapper movieMapper;

    public MovieController(MovieService movieService, MovieMapper movieMapper) {
        this.movieService = movieService;
        this.movieMapper = movieMapper;
    }

    private MovieDTO getMovieDTOWithDetails(Movie movie) {
        MovieDTO movieDTO = movieMapper.toDTO(movie);
        try {
            List<RatingDTO> ratings = movieService.getRatingsForMovie(movie.getMovieId());
            System.out.println("Fetched " + ratings.size() + " ratings for movie " + movie.getMovieId());

        } catch (RuntimeException e) {
            System.err.println("Error fetching ratings for movie " + movie.getMovieId() + ": " + e.getMessage());
        }

        return movieDTO;
    }


    @GetMapping
    public ResponseEntity<List<MovieDTO>> getAllMovies() {
        List<Movie> movies = movieService.findAll();

        List<MovieDTO> movieDTOs = movies.stream()
                .map(this::getMovieDTOWithDetails)
                .collect(Collectors.toList());

        return ResponseEntity.ok(movieDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable(name = "id") Long id) {
        Optional<Movie> movieOpt = movieService.findById(id);
        if (movieOpt.isPresent()) {
            Movie movie = movieOpt.get();
            MovieDTO movieDTO = getMovieDTOWithDetails(movie);
            return ResponseEntity.ok(movieDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/title/{title}")
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
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MovieDTO> createMovie(@Valid @RequestBody MovieDTO movieDTO) throws DuplicateResourceException {
        Movie movie = movieMapper.toEntity(movieDTO);
        Movie createdMovie = movieService.save(movie);
        MovieDTO createdMovieDTO = movieMapper.toDTO(createdMovie);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovieDTO);
    }


    @PutMapping("/{id}")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable(name = "id") Long id, @Valid @RequestBody MovieDTO movieDTO) throws ResourceNotFoundException {
        Optional<Movie> existingMovieOpt = movieService.findById(id);
        if (existingMovieOpt.isEmpty()) {
            throw new ResourceNotFoundException("Movie with id " + id + " not found");
        }
        Movie existingMovie = existingMovieOpt.get();

        movieMapper.updateEntityFromDTO(movieDTO, existingMovie);

        Movie updatedMovie = movieService.updateMovie(id, existingMovie);
        MovieDTO updatedMovieDTO = movieMapper.toDTO(updatedMovie);
        return ResponseEntity.ok(updatedMovieDTO);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable(name = "id") Long id) throws ResourceNotFoundException {
        movieService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}