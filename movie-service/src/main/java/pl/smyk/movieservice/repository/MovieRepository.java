package pl.smyk.movieservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.smyk.movieservice.model.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
}
