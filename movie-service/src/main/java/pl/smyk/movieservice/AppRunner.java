package pl.smyk.movieservice;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.smyk.movieservice.model.Genre;
import pl.smyk.movieservice.model.Movie;
import pl.smyk.movieservice.repository.GenreRepository;
import pl.smyk.movieservice.repository.MovieRepository;

import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AppRunner implements CommandLineRunner {

    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create genres
        Genre action = Genre.builder().name("Action").build();
        Genre comedy = Genre.builder().name("Comedy").build();
        Genre drama = Genre.builder().name("Drama").build();
        Genre horror = Genre.builder().name("Horror").build();

        genreRepository.saveAll(Set.of(action, comedy, drama, horror));

        // Create movies
        Movie movie1 = Movie.builder()
                .title("Movie 1")
                .description("Description for Movie 1")
                .durationMinutes(120)
                .releaseDate(LocalDate.of(2023, 1, 15))
                .posterUrl("http://example.com/poster1.jpg")
                .ageRating("PG-13")
                .genres(Set.of(action, drama))
                .build();

        Movie movie2 = Movie.builder()
                .title("Movie 2")
                .description("Description for Movie 2")
                .durationMinutes(90)
                .releaseDate(LocalDate.of(2022, 5, 20))
                .posterUrl("http://example.com/poster2.jpg")
                .ageRating("PG")
                .genres(Set.of(comedy))
                .build();

        movieRepository.saveAll(Set.of(movie1, movie2));
    }
}
