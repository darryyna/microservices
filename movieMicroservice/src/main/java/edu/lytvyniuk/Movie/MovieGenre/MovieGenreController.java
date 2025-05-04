package edu.lytvyniuk.Movie.MovieGenre;

/*
  @author darin
  @project microservices
  @class MovieGenreController
  @version 1.0.0
  @since 28.04.2025 - 14.00
*/

import edu.lytvyniuk.DTOs.GenreDTO;
import edu.lytvyniuk.DTOs.MovieGenreDTO;
import edu.lytvyniuk.Movie.Genre.Genre;
import edu.lytvyniuk.Movie.Genre.GenreService;
import edu.lytvyniuk.Movie.Movie;
import edu.lytvyniuk.Movie.MovieService;
import edu.lytvyniuk.customException.DuplicateResourceException;
import edu.lytvyniuk.customException.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/movieGenres")
public class MovieGenreController {
    private final MovieGenreService movieGenreService;
    private final MovieGenreMapper movieGenreMapper;
    private final MovieService movieService;
    private final GenreService genreService;

    @Autowired
    public MovieGenreController(MovieGenreService movieGenreService, MovieGenreMapper movieGenreMapper,
                                MovieService movieService, GenreService genreService) {
        this.movieGenreService = movieGenreService;
        this.movieGenreMapper = movieGenreMapper;
        this.movieService = movieService;
        this.genreService = genreService;
    }

    @GetMapping
    public ResponseEntity<List<MovieGenreDTO>> getAllMovieGenres() {
        List<MovieGenre> movieGenres = movieGenreService.findAll();

        List<MovieGenreDTO> movieGenreDTOs = new ArrayList<>();

        for (MovieGenre movieGenre : movieGenres) {
            try {
                Movie movie = movieGenre.getMovie();
                List<Genre> genres = movieGenreService.findGenresByMovieId(movie.getMovieId());
                movieGenreDTOs.add(movieGenreMapper.toDTO(movie, genres));
            } catch (ResourceNotFoundException e) {
                e.printStackTrace();
            }
        }

        return ResponseEntity.ok(movieGenreDTOs);
    }

    @GetMapping("/movie/{movieId}/details")
    public ResponseEntity<MovieGenreDTO> getMovieWithGenres(@PathVariable(name = "movieId") Long movieId) throws ResourceNotFoundException {
        Movie movie = movieGenreService.findMovieById(movieId);
        List<Genre> genres = movieGenreService.findGenresByMovieId(movieId);
        MovieGenreDTO dto = movieGenreMapper.toDTO(movie, genres);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/movie/{title}")
    public ResponseEntity<MovieGenreDTO> getMovieWithGenresByTitle(@PathVariable(name = "title") String title) throws ResourceNotFoundException {
        Movie movie = movieGenreService.findMovieByTitle(title);
        if (movie == null) {
            throw new ResourceNotFoundException("Movie with title " + title + " not found");
        }
        List<Genre> genres = movieGenreService.findGenresByMovieId(movie.getMovieId());
        MovieGenreDTO dto = movieGenreMapper.toDTO(movie, genres);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<List<MovieGenreDTO>> createMovieGenre(@Valid @RequestBody MovieGenreDTO movieGenreDTO) throws ResourceNotFoundException, DuplicateResourceException {
        List<MovieGenreDTO> createdDTOs = new ArrayList<>();
        List<GenreDTO> genreDTOs = new ArrayList<>(movieGenreDTO.getGenres());

        for (GenreDTO genreDTO : genreDTOs) {
            MovieGenreDTO singleGenreDTO = new MovieGenreDTO();
            singleGenreDTO.setMovieTitle(movieGenreDTO.getMovieTitle());
            singleGenreDTO.setGenres(List.of(genreDTO));

            MovieGenre movieGenre = movieGenreMapper.toEntity(singleGenreDTO, movieService, genreService);
            MovieGenre createdMovieGenre = movieGenreService.createMovieGenre(movieGenre);

            Movie movie = createdMovieGenre.getMovie();
            List<Genre> genres = List.of(createdMovieGenre.getGenre());
            MovieGenreDTO createdDTO = movieGenreMapper.toDTO(movie, genres);
            createdDTOs.add(createdDTO);
        }

        return new ResponseEntity<>(createdDTOs, HttpStatus.CREATED);
    }
}
