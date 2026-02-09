package pl.smyk.cinemaservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.smyk.cinemaservice.model.Hall;

@Repository
public interface HallRepository extends JpaRepository<Hall, Long> {

    boolean existsByNameAndCinemaId(String name, Long cinemaId);
}