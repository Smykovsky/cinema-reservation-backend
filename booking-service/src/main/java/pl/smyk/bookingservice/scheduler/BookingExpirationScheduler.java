package pl.smyk.bookingservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.smyk.bookingservice.model.Booking;
import pl.smyk.bookingservice.model.BookingStatus;
import pl.smyk.bookingservice.repository.BookingRepository;
import pl.smyk.bookingservice.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingExpirationScheduler {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    // This task runs every minute
    @Scheduled(fixedRate = 60000) // 60 seconds
    public void expirePendingBookings() {
        log.info("Running scheduled task to expire pending bookings...");
        List<Booking> pendingBookings = bookingRepository.findByStatusAndExpiresAtBefore(BookingStatus.PENDING, LocalDateTime.now());

        if (!pendingBookings.isEmpty()) {
            log.info("Found {} pending bookings to expire.", pendingBookings.size());
            pendingBookings.forEach(booking -> {
                try {
                    bookingService.expireBooking(booking.getId());
                    log.info("Booking {} expired successfully.", booking.getId());
                } catch (Exception e) {
                    log.error("Error expiring booking {}: {}", booking.getId(), e.getMessage());
                }
            });
        } else {
            log.info("No pending bookings to expire.");
        }
    }
}
