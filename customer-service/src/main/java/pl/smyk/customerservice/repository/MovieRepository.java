package pl.smyk.customerservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import pl.smyk.customerservice.model.Movie;

import java.time.LocalDate;
import java.util.List;

public interface MovieRepository extends MongoRepository<Movie, String> {
    @Query("{ 'playDates': { $gte: ?0, $lte: ?1 } }")
    List<Movie> findByPlayDatesBetween(LocalDate start, LocalDate end);

    List<Movie> findByGenre(String genre);
    List<Movie> findAllByTitleContainingIgnoreCase(String title);
}
