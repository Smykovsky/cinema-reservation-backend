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
        if (movieRepository.count() == 0) {
            List<Movie> movies = List.of(
                    Movie.builder()
                            .title("The Shawshank Redemption")
                            .description("Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.")
                            .duration(142)
                            .genre("Drama")
                            .releaseDate(LocalDate.of(1994, 9, 23))
                            .build(),
                    Movie.builder()
                            .title("The Godfather")
                            .description("The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.")
                            .duration(175)
                            .genre("Crime, Drama")
                            .releaseDate(LocalDate.of(1972, 3, 24))
                            .build(),
                    Movie.builder()
                            .title("The Dark Knight")
                            .description("When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.")
                            .duration(152)
                            .genre("Action, Crime, Drama")
                            .releaseDate(LocalDate.of(2008, 7, 18))
                            .build(),
                    Movie.builder()
                            .title("Pulp Fiction")
                            .description("The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.")
                            .duration(154)
                            .genre("Crime, Drama")
                            .releaseDate(LocalDate.of(1994, 10, 14))
                            .build()
            );
            movieRepository.saveAll(movies);
        }
    }
}