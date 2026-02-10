package pl.smyk.cinemaservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import pl.smyk.cinemaservice.model.Screening;

import pl.smyk.cinemaservice.model.ScreeningSeat;

import pl.smyk.cinemaservice.model.ScreeningSeatId;

import java.util.List;
import java.util.Set;


@Repository

public interface ScreeningSeatRepository extends JpaRepository<ScreeningSeat, ScreeningSeatId> {

    List<ScreeningSeat> findByScreening(Screening screening);
    List<ScreeningSeat> findByScreeningAndSeatIdIn(Screening screening, Set<Long> seatIds);

}
