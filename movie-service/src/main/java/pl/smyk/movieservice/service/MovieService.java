package pl.smyk.movieservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.smyk.movieservice.dto.CreateMovieRequest;
import pl.smyk.movieservice.dto.MovieDto;
import pl.smyk.movieservice.dto.UpdateMovieRequest;
import pl.smyk.movieservice.exception.MovieAlreadyExistsException;
import pl.smyk.movieservice.exception.MovieNotFoundException;
import pl.smyk.movieservice.mapper.MovieMapper;
import pl.smyk.movieservice.model.Genre;
import pl.smyk.movieservice.model.Movie;
import pl.smyk.movieservice.repository.GenreRepository;
import pl.smyk.movieservice.repository.MovieRepository;
import pl.smyk.movieservice.specification.MovieSpecification;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final MovieMapper movieMapper;

    public Page<MovieDto> getAllMovies(
            String title,
            String genre,
            Integer minDuration,
            Integer maxDuration,
            LocalDate releaseDateFrom,
            LocalDate releaseDateTo,
            Pageable pageable) {

        Specification<Movie> spec = Specification
                .where(MovieSpecification.hasTitle(title))
                .and(MovieSpecification.hasGenre(genre))
                .and(MovieSpecification.hasDurationGreaterThanOrEqual(minDuration))
                .and(MovieSpecification.hasDurationLessThanOrEqual(maxDuration))
                .and(MovieSpecification.hasReleaseDateAfterOrEqual(releaseDateFrom))
                .and(MovieSpecification.hasReleaseDateBeforeOrEqual(releaseDateTo));

        Page<Movie> movies = movieRepository.findAll(spec, pageable);
        return movies.map(movieMapper::toDto);
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

        // Pobierz gatunki z bazy danych
        Set<Genre> genres = createMovieRequest.getGenreIds().stream()
                .map(id -> genreRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Genre not found with id: " + id)))
                .collect(Collectors.toSet());

        movie.setGenres(genres);

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

        // Pobierz gatunki z bazy danych
        Set<Genre> genres = updateMovieRequest.getGenreIds().stream()
                .map(id -> genreRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Genre not found with id: " + id)))
                .collect(Collectors.toSet());

        updatedMovie.setGenres(genres);

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