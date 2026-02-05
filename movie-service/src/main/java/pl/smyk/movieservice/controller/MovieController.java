package pl.smyk.movieservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.smyk.movieservice.dto.CreateMovieRequest;
import pl.smyk.movieservice.dto.MovieDto;
import pl.smyk.movieservice.dto.UpdateMovieRequest;
import pl.smyk.movieservice.service.MovieService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<Page<MovieDto>> getAllMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDateTo,
            @PageableDefault(size = 20, sort = "releaseDate", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(movieService.getAllMovies(
                title, genre, minDuration, maxDuration,
                releaseDateFrom, releaseDateTo, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @PostMapping
    public ResponseEntity<MovieDto> createMovie(@Valid @RequestBody CreateMovieRequest createMovieRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.createMovie(createMovieRequest));
    }

    @PutMapping
    public ResponseEntity<MovieDto> updateMovie(@Valid @RequestBody UpdateMovieRequest updateMovieRequest) {
        return ResponseEntity.ok(movieService.updateMovie(updateMovieRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}