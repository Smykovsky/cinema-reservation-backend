package pl.smyk.bookingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.smyk.bookingservice.model.Booking;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingNumber(String bookingNumber);
}