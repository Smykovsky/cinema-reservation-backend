package pl.smyk.customerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.smyk.customerservice.dto.MovieDto;
import pl.smyk.customerservice.dto.response.ReservationResponse;
import pl.smyk.customerservice.mapper.MovieMapper;
import pl.smyk.customerservice.model.Movie;
import pl.smyk.customerservice.repository.MovieRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;

    public Movie findById(String id) {
        return movieRepository.findById(id).orElseThrow();
    }

    public MovieDto getMovieById(String id) {
        Movie movie = findById(id);
        if (movie != null) {
            return MovieMapper.INSTANCE.movieToMovieDto(movie);
        }
        return null;
    }

    public List<MovieDto> getAllMovies() {
      List <Movie> movieList = movieRepository.findAll();
      return movieList.stream().map(MovieMapper.INSTANCE::movieToMovieDto).collect(Collectors.toList());
    }

    public List<MovieDto> getMoviesByPlayDatesBetween(LocalDate start, LocalDate end) {
        List<Movie> movies = movieRepository.findByPlayDatesBetween(start, end);
        return movies.stream().map(MovieMapper.INSTANCE::movieToMovieDto).toList();
    }

    public List<MovieDto> getMoviesByGenre(String genre) {
        List<Movie> movies = movieRepository.findByGenre(genre);
        return movies.stream().map(MovieMapper.INSTANCE::movieToMovieDto).toList();
    }

  public List<MovieDto> getMoviesByTitlePhrase(String title) {
    List<Movie> movies = movieRepository.findAllByTitleContainingIgnoreCase(title);
    return movies.stream().map(MovieMapper.INSTANCE::movieToMovieDto).toList();
  }

}
