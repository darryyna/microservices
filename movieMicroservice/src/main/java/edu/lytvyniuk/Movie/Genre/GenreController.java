package edu.lytvyniuk.Movie.Genre;

/*
  @author darin
  @project microservices
  @class GenreController
  @version 1.0.0
  @since 28.04.2025 - 13.59
*/

import edu.lytvyniuk.DTOs.GenreDTO;
import edu.lytvyniuk.customException.DuplicateResourceException;
import edu.lytvyniuk.customException.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;
    private final GenreMapper genreMapper;

    @Autowired
    public GenreController(GenreService genreService, GenreMapper genreMapper) {
        this.genreService = genreService;
        this.genreMapper = genreMapper;
    }

    @GetMapping
    public ResponseEntity<List<GenreDTO>> getAllGenres() {
        return ResponseEntity.ok(genreMapper.toDTOList(genreService.findAll()));
    }

    @GetMapping("/{name}")
    public ResponseEntity<GenreDTO> getGenreByName(@PathVariable(name = "name") String name) throws ResourceNotFoundException {
        Genre genre = genreService.findByName(name);
        return genre != null ?
                ResponseEntity.ok(genreMapper.toDTO(genre)) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping
    public ResponseEntity<GenreDTO> createGenre(@Valid @RequestBody GenreDTO genreDTO) throws DuplicateResourceException {
        Genre savedGenre = genreService.save(genreMapper.toEntity(genreDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(genreMapper.toDTO(savedGenre));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable(name = "id") Long id) throws ResourceNotFoundException {
        genreService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
