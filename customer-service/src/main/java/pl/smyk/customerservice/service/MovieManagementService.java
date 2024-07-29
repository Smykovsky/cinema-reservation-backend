package pl.smyk.customerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.smyk.customerservice.dto.MovieDto;
import pl.smyk.customerservice.dto.response.MovieResponse;
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
        return movieRepository.findById(id).orElse(null);
    }

    private void saveMovie(Movie movie) {
        movieRepository.save(movie);
    }


    //add movie
    public MovieResponse addMovie(MovieDto movieDto) {
        if (movieRepository.findByTitle(movieDto.getTitle()).isPresent()) {
            return MovieResponse.builder()
                    .status(409)
                    .message("Movie with title provided already exists!")
                    .build();
        }

        Movie movie = Movie.builder()
                .title(movieDto.getTitle())
                .genres(movieDto.getGenres())
                .description(movieDto.getDescription())
                .playingRoom(movieDto.getPlayingRoom())
                .playDates(movieDto.getPlayDates())
                .playTimes(movieDto.getPlayTimes())
                .build();

        saveMovie(movie);

        return MovieResponse.builder()
                .status(200)
                .message("Movie added successfully!")
                .build();
    }

    //update movie
    public MovieResponse updateMovie(MovieDto movieDto) {
        Movie movie = findById(movieDto.getId());

        if (movie == null) {
            return MovieResponse.builder()
                    .status(404)
                    .message("Movie not found!")
                    .build();
        }

        Optional.ofNullable(movieDto.getTitle()).filter(title -> !title.isEmpty()).ifPresent(movie::setTitle);
        Optional.ofNullable(movieDto.getGenres()).filter(genre -> !genre.isEmpty()).ifPresent(movie::setGenres);
        Optional.ofNullable(movieDto.getDescription()).filter(description -> !description.isEmpty()).ifPresent(movie::setDescription);
        Optional.ofNullable(movieDto.getPlayingRoom()).filter(playingRoom -> playingRoom != null && playingRoom != 0L).ifPresent(movie::setPlayingRoom);
        Optional.ofNullable(movieDto.getPlayDates()).filter(dates -> !dates.isEmpty()).ifPresent(movie::setPlayDates);
        Optional.ofNullable(movieDto.getPlayTimes()).filter(times -> !times.isEmpty()).ifPresent(movie::setPlayTimes);

        movieRepository.save(movie);

        return MovieResponse.builder()
                .status(200)
                .message("Movie successfully updated!")
                .build();

    }

    //delete movie
    public MovieResponse deleteMovie(String id) {
        Movie movie = findById(id);
        if (movie == null) {
            return MovieResponse.builder()
                    .status(404)
                    .message("Movie not found!")
                    .build();
        }

        movieRepository.delete(movie);
        return MovieResponse.builder()
                .status(200)
                .message("Movie successfully deleted!")
                .build();
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
