package pl.smyk.cinemaservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.smyk.cinemaservice.model.ScreeningSeat;
import pl.smyk.cinemaservice.model.ScreeningSeatId;

@Repository
public interface ScreeningSeatRepository extends JpaRepository<ScreeningSeat, ScreeningSeatId> {
}