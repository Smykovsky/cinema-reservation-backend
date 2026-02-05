package pl.smyk.movieservice;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.smyk.movieservice.model.Movie;
import pl.smyk.movieservice.repository.MovieRepository;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AppRunner implements CommandLineRunner {

    private final MovieRepository movieRepository;

    @Override
    public void run(String... args) throws Exception {

    }
}