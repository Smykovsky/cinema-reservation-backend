package pl.smyk.cinemaservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.smyk.cinemaservice.model.Seat;
import pl.smyk.cinemaservice.model.SeatType;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByHallId(Long hallId);

    List<Seat> findByHallIdAndSeatType(Long hallId, SeatType seatType);

    Optional<Seat> findByHallIdAndRowAndNumber(Long hallId, Integer row, Integer number);

    boolean existsByHallIdAndRowAndNumber(Long hallId, Integer row, Integer number);
}