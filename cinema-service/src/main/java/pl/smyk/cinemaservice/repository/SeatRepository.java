package pl.smyk.cinemaservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.smyk.cinemaservice.model.Seat;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    boolean existsByHallIdAndRowAndNumber(Long hallId, Integer row, Integer number);
}