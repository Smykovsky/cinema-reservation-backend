package pl.smyk.customerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.smyk.customerservice.dto.MovieDto;
import pl.smyk.customerservice.dto.response.ReservationResponse;
import pl.smyk.customerservice.model.Movie;
import pl.smyk.customerservice.repository.MovieRepository;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;

    public void saveMovie(Movie movie) {
        movieRepository.save(movie);
    }

    public void deleteMovie(Movie movie) {
        movieRepository.delete(movie);
    }

    public Movie findById(String id) {
        return movieRepository.findById(id).orElseThrow();
    }

}
