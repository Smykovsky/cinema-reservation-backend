package pl.smyk.cinemaservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.smyk.cinemaservice.model.Cinema;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {

    boolean existsByName(String name);
}