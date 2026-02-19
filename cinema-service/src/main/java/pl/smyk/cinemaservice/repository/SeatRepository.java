package pl.smyk.cinemaservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import pl.smyk.cinemaservice.model.Hall;

import pl.smyk.cinemaservice.model.Seat;



import java.util.List;



@Repository

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findAllByHallId(Long hallId);
    boolean existsByHallIdAndRowAndNumber(Long hallId, Integer row, Integer number);

    List<Seat> findByHall(Hall hall);

}
