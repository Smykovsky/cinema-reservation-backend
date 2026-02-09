package pl.smyk.cinemaservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.smyk.cinemaservice.model.Seat;
import pl.smyk.cinemaservice.model.SeatType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByHallId(UUID hallId);

    List<Seat> findByHallIdAndSeatType(UUID hallId, SeatType seatType);

    Optional<Seat> findByHallIdAndRowAndNumber(UUID hallId, String row, Integer number);

    boolean existsByHallIdAndRowAndNumber(UUID hallId, String row, Integer number);
}