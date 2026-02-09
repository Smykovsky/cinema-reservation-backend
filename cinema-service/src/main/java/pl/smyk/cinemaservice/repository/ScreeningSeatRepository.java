package pl.smyk.cinemaservice.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.smyk.cinemaservice.model.ScreeningSeat;
import pl.smyk.cinemaservice.model.ScreeningSeatId;
import pl.smyk.cinemaservice.model.SeatStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScreeningSeatRepository extends JpaRepository<ScreeningSeat, ScreeningSeatId> {

    // Znajdź wszystkie miejsca dla seansu
    List<ScreeningSeat> findByScreeningId(Long screeningId);

    // Znajdź miejsca z pessimistic lock (dla rezerwacji!)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ss FROM ScreeningSeat ss " +
            "WHERE ss.screening.id = :screeningId " +
            "AND ss.seat.id IN :seatIds")
    List<ScreeningSeat> findByScreeningIdAndSeatIdInWithLock(
            @Param("screeningId") UUID screeningId,
            @Param("seatIds") List<UUID> seatIds
    );

    // Znajdź wygasłe rezerwacje
    @Query("SELECT ss FROM ScreeningSeat ss " +
            "WHERE ss.status = :status " +
            "AND ss.reservedUntil < :now")
    List<ScreeningSeat> findExpiredReservations(
            @Param("status") SeatStatus status,
            @Param("now") Instant now
    );

    // Policz dostępne miejsca
    @Query("SELECT COUNT(ss) FROM ScreeningSeat ss " +
            "WHERE ss.screening.id = :screeningId " +
            "AND ss.status = 'AVAILABLE'")
    long countAvailableSeats(@Param("screeningId") UUID screeningId);

    // Znajdź rezerwacje użytkownika
    List<ScreeningSeat> findByReservedBy(String reservedBy);
}