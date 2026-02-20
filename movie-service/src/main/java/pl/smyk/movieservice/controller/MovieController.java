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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.smyk.common.dto.BatchMovieRequest;
import pl.smyk.common.dto.MovieDto;
import pl.smyk.movieservice.dto.*;
import pl.smyk.movieservice.service.MovieService;

import java.time.LocalDate;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<PageResponse<MovieDto>> getAllMovies(
            MovieFilterDto filter,
            @PageableDefault(size = 20, sort = "releaseDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<MovieDto> page = movieService.getAllMovies(filter, pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<MovieDto>> getMoviesByIds(@RequestBody BatchMovieRequest batchMovieRequest) {
        return ResponseEntity.ok(movieService.getMoviesByIds(batchMovieRequest));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<MovieDto> createMovie(
            @RequestPart("movie") @Valid CreateMovieRequest createMovieRequest,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.createMovie(createMovieRequest, imageFile));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping
    public ResponseEntity<MovieDto> updateMovie(
            @RequestPart("movie") @Valid UpdateMovieRequest updateMovieRequest,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        return ResponseEntity.ok(movieService.updateMovie(updateMovieRequest, imageFile));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}