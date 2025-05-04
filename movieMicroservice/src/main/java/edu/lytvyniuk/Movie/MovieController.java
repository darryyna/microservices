package edu.lytvyniuk.Movie;

/*
  @author darin
  @project microservices
  @class MovieController
  @version 1.0.0
  @since 28.04.2025 - 13.42
*/

import edu.lytvyniuk.DTOs.GenreDTO;
import edu.lytvyniuk.DTOs.MovieDTO;
import edu.lytvyniuk.DTOs.RatingDTO;
import edu.lytvyniuk.Movie.Genre.Genre;
import edu.lytvyniuk.Movie.MovieGenre.MovieGenreService;
import edu.lytvyniuk.customException.DuplicateResourceException;
import edu.lytvyniuk.customException.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieService movieService;
    private final MovieGenreService movieGenreService;
    private final MovieMapper movieMapper;

    public MovieController(MovieService movieService, MovieGenreService movieGenreService , MovieMapper movieMapper) {
        this.movieService = movieService;
        this.movieGenreService = movieGenreService;
        this.movieMapper = movieMapper;
    }

    private MovieDTO getMovieDTOWithDetails(Movie movie) {
        MovieDTO movieDTO = movieMapper.toDTO(movie);

        List<GenreDTO> genreDTOs = new ArrayList<>();
        try {
            List<Genre> genres = movieGenreService.findGenresByMovieId(movie.getMovieId());
            genreDTOs = genres.stream().map(g -> {
                GenreDTO dto = new GenreDTO();
                dto.setGenreId(g.getGenreId());
                dto.setName(g.getName());
                return dto;
            }).toList();
        } catch (ResourceNotFoundException e) {
            // Логування помилки або повернення порожнього списку
            System.err.println("Genres not found for movieId " + movie.getMovieId() + ": " + e.getMessage());
        }

        movieDTO.setGenres(genreDTOs);
        return movieDTO;
    }

    @GetMapping
    public ResponseEntity<List<MovieDTO>> getAllMovies() {
        List<Movie> movies = movieService.findAll();
        List<Movie> safeMovies = new ArrayList<>(movies);

        List<MovieDTO> movieDTOs = safeMovies.stream()
                .map(this::getMovieDTOWithDetails)
                .collect(Collectors.toList());

        return ResponseEntity.ok(movieDTOs);
    }

    // for rating service
    @GetMapping("/titles")
    public ResponseEntity<Map<Long, String>> getMovieTitlesByIds(@RequestParam("ids") List<Long> ids) {
        if (ids.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.emptyMap());
        }
        System.out.println("Received movie IDs: " + ids);
        Map<Long, String> titles = movieService.getMovieTitlesByIds(ids);
        return ResponseEntity.ok(titles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable(name = "id") Long id) throws ResourceNotFoundException {
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
    public ResponseEntity<MovieDTO> getMovieByTitle(@PathVariable(name = "title") String title) throws ResourceNotFoundException {
        List<Movie> movies = movieService.findByTitle(title);
        if (movies.isEmpty()) {
            throw new ResourceNotFoundException("Movie with title " + title + " not found");
        }

        MovieDTO movieDTO = getMovieDTOWithDetails(movies.get(0));
        return ResponseEntity.ok(movieDTO);
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
        List<GenreDTO> genreDTOs = movieMapper.extractGenreDTOs(movieDTO);

        Movie createdMovie = movieService.save(movie, genreDTOs);
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