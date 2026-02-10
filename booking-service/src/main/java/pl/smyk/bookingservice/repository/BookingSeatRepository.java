package pl.smyk.bookingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.smyk.bookingservice.model.Booking;
import pl.smyk.bookingservice.model.BookingSeat;

import java.util.List;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
    List<BookingSeat> findByBooking(Booking booking);
}