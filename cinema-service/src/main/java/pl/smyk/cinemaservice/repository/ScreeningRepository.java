package pl.smyk.cinemaservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.smyk.cinemaservice.model.Screening;

import java.time.Instant;
import java.util.List;

@Repository
public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    @Query("SELECT s FROM Screening s " +
            "JOIN s.hall h " +
            "WHERE h.cinema.id = :cinemaId " +
            "AND s.startTime >= :startOfDay " +
            "AND s.startTime < :endOfDay " +
            "ORDER BY s.startTime ASC")
    List<Screening> findByCinemaIdAndDateRange(
            @Param("cinemaId") Long cinemaId,
            @Param("startOfDay") Instant startOfDay,
            @Param("endOfDay") Instant endOfDay
    );

    List<Screening> findByHallIdAndStartTimeBeforeAndEndTimeAfter(Long hallId, Instant endTime, Instant startTime);
}