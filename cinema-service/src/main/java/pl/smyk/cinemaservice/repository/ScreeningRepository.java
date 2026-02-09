package pl.smyk.cinemaservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.smyk.cinemaservice.model.Screening;

import java.time.Instant;
import java.util.List;

@Repository
public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    List<Screening> findByHallIdAndStartTimeBeforeAndEndTimeAfter(Long hallId, Instant endTime, Instant startTime);
}