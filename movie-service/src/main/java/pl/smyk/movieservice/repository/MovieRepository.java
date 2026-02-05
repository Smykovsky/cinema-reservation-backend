package pl.smyk.movieservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pl.smyk.movieservice.model.Movie;

import java.util.Optional;

public interface MovieRepository extends
        JpaRepository<Movie, Long>,
        JpaSpecificationExecutor<Movie> {

    Optional<Movie> findByTitle(String title);
}