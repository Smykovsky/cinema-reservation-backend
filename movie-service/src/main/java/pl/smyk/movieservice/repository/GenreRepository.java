package pl.smyk.movieservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.smyk.movieservice.model.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
}
