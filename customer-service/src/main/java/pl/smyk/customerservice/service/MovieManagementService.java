package pl.smyk.customerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.smyk.customerservice.dto.MovieDto;
import pl.smyk.customerservice.mapper.MovieMapper;
import pl.smyk.customerservice.model.Movie;
import pl.smyk.customerservice.model.PlayTime;
import pl.smyk.customerservice.repository.MovieRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieManagementService {
    private final MovieRepository movieRepository;

    private Movie findById(String id) {
        return movieRepository.findById(id).orElseThrow();
    }

    private void saveMovie(Movie movie) {
        movieRepository.save(movie);
    }


    //add movie
    public MovieDto addMovie(MovieDto movieDto) {
        Movie movie = Movie.builder()
                .title(movieDto.getTitle())
                .genre(movieDto.getGenre())
                .description(movieDto.getDescription())
                .playDates(movieDto.getPlayDates())
                .build();

        saveMovie(movie);

        return MovieMapper.INSTANCE.movieToMovieDto(movie);
    }

    //update movie
    public MovieDto updateMovie(MovieDto movieDto) {
        Movie movie = findById(movieDto.getId());

        Optional.ofNullable(movieDto.getTitle()).filter(title -> !title.isEmpty()).ifPresent(movie::setTitle);
        Optional.ofNullable(movieDto.getGenre()).filter(genre -> !genre.isEmpty()).ifPresent(movie::setGenre);
        Optional.ofNullable(movieDto.getDescription()).filter(description -> !description.isEmpty()).ifPresent(movie::setDescription);
        Optional.ofNullable(movieDto.getPlayDates()).filter(dates -> !dates.isEmpty()).ifPresent(movie::setPlayDates);
        Optional.ofNullable(movieDto.getPlayTimes()).filter(times -> !times.isEmpty()).ifPresent(movie::setPlayTimes);

        movieRepository.save(movie);

        return MovieMapper.INSTANCE.movieToMovieDto(movie);
    }

    //delete movie
    public void deleteMovie(String movieId) {
        Movie movie = findById(movieId);
        if (movie == null) {
            return;
        }

        movieRepository.delete(movie);
    }

    public void addPlayTimesToMovie(String movieId, List<PlayTime> playTimes) {
        Movie movie = findById(movieId);
        if (movie == null) {
            return;
        }

        for (PlayTime playTime : playTimes) {
            movie.addPlayTime(playTime);
        }

        saveMovie(movie);
    }

    public void removePlayTimesFromMovie(String movieId, List<PlayTime> playTimes) {
        Movie movie = findById(movieId);
        if (movie == null) {
            return;
        }

        for (PlayTime playTime : playTimes) {
            movie.removePlayTime(playTime);
        }

        saveMovie(movie);
    }
}
