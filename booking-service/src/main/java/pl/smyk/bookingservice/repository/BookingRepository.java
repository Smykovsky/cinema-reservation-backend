package pl.smyk.bookingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.smyk.bookingservice.model.Booking;
import pl.smyk.bookingservice.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingNumber(String bookingNumber);
    Optional<List<Booking>> findByUserId(Long userId);
    List<Booking> findByStatusAndExpiresAtBefore(BookingStatus status, LocalDateTime expiresAt);
}