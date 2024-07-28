package pl.smyk.customerservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.smyk.customerservice.dto.MovieDto;
import pl.smyk.customerservice.dto.request.MovieDateRangeDto;
import pl.smyk.customerservice.model.Movie;
import pl.smyk.customerservice.service.MovieService;

import java.util.List;

@RestController
@RequestMapping("/api/movie")
@RequiredArgsConstructor
public class MovieController {
  private final MovieService movieService;


  @GetMapping("/{id}")
  public ResponseEntity<?> getMovieById(@PathVariable String id) {
    MovieDto movieDto = movieService.getMovieById(id);
    return ResponseEntity.ok(movieDto);
  }
  @GetMapping()
  public ResponseEntity<?> getAllMovies() {
    List<MovieDto> movieList = movieService.getAllMovies();
    return ResponseEntity.ok(movieList);
  }

  @GetMapping("/search/between-dates")
  public ResponseEntity<?> getMoviesByPlayDatesBetween(@RequestBody MovieDateRangeDto request) {
    List<MovieDto> movieList = movieService.getMoviesByPlayDatesBetween(request.getStart(), request.getEnd());
    return ResponseEntity.ok(movieList);
  }

  @GetMapping("/search/title")
  public ResponseEntity<?> getMoviesByTitle(@RequestBody String title) {
    List<MovieDto> movieList = movieService.getMoviesByTitlePhrase(title);
    return ResponseEntity.ok(movieList);
  }

  @GetMapping("/search/genre")
  public ResponseEntity<?> getMoviesByGenre(@RequestBody String genre) {
    List<MovieDto> movieList = movieService.getMoviesByGenre(genre);
    return ResponseEntity.ok(movieList);
  }

}
