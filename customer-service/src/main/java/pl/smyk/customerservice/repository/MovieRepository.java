package pl.smyk.customerservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import pl.smyk.customerservice.model.Genre;
import pl.smyk.customerservice.model.Movie;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MovieRepository extends MongoRepository<Movie, String> {
    @Query("{ 'playDates': { $gte: ?0, $lte: ?1 } }")
    List<Movie> findByPlayDatesBetween(LocalDate start, LocalDate end);

    List<Movie> findByGenres(List<Genre> genres);
    List<Movie> findAllByTitleContainingIgnoreCase(String title);
    Optional<Movie> findByTitle(String title);
}
