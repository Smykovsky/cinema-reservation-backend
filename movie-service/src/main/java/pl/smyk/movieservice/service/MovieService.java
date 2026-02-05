package pl.smyk.movieservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.smyk.movieservice.dto.CreateMovieRequest;
import pl.smyk.movieservice.dto.MovieDto;
import pl.smyk.movieservice.dto.UpdateMovieRequest;
import pl.smyk.movieservice.exception.MovieAlreadyExistsException;
import pl.smyk.movieservice.exception.MovieNotFoundException;
import pl.smyk.movieservice.mapper.MovieMapper;
import pl.smyk.movieservice.model.Movie;
import pl.smyk.movieservice.repository.MovieRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public List<MovieDto> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        return movieMapper.toDtoList(movies);
    }

    public MovieDto getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie with id " + id + " not found"));
        return movieMapper.toDto(movie);
    }

    public MovieDto createMovie(CreateMovieRequest createMovieRequest) {
        if (movieRepository.findByTitle(createMovieRequest.getTitle()).isPresent()) {
            throw new MovieAlreadyExistsException("Movie with title " + createMovieRequest.getTitle() + " already exists");
        }
        Movie movie = movieMapper.toEntity(createMovieRequest);
        Movie savedMovie = movieRepository.save(movie);
        return movieMapper.toDto(savedMovie);
    }

    public MovieDto updateMovie(UpdateMovieRequest updateMovieRequest) {
        Movie existingMovie = movieRepository.findById(updateMovieRequest.getId())
                .orElseThrow(() -> new MovieNotFoundException("Movie with id " + updateMovieRequest.getId() + " not found"));

        if (movieRepository.findByTitle(updateMovieRequest.getTitle()).isPresent() &&
                !existingMovie.getTitle().equals(updateMovieRequest.getTitle())) {
            throw new MovieAlreadyExistsException("Movie with title " + updateMovieRequest.getTitle() + " already exists");
        }

        Movie updatedMovie = movieMapper.toEntity(updateMovieRequest);
        updatedMovie.setId(existingMovie.getId());
        Movie savedMovie = movieRepository.save(updatedMovie);
        return movieMapper.toDto(savedMovie);
    }

    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new MovieNotFoundException("Movie with id " + id + " not found");
        }
        movieRepository.deleteById(id);
    }
}