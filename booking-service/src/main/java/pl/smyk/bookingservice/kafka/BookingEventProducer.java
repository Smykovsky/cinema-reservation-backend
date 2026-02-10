package pl.smyk.bookingservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.smyk.bookingservice.dto.BookingDetailsResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingEventProducer {

    private static final String TOPIC_BOOKING_CREATED = "booking_created";
    private static final String TOPIC_BOOKING_CONFIRMED = "booking_confirmed";
    private static final String TOPIC_BOOKING_CANCELLED = "booking_cancelled";
    private static final String TOPIC_BOOKING_EXPIRED = "booking_expired";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendBookingCreatedEvent(BookingDetailsResponse booking) {
        log.info("Producing booking created event for booking ID: {}", booking.getId());
        kafkaTemplate.send(TOPIC_BOOKING_CREATED, booking.getId().toString(), booking);
    }

    public void sendBookingConfirmedEvent(Long bookingId) {
        log.info("Producing booking confirmed event for booking ID: {}", bookingId);
        kafkaTemplate.send(TOPIC_BOOKING_CONFIRMED, bookingId.toString(), bookingId);
    }

    public void sendBookingCancelledEvent(Long bookingId) {
        log.info("Producing booking cancelled event for booking ID: {}", bookingId);
        kafkaTemplate.send(TOPIC_BOOKING_CANCELLED, bookingId.toString(), bookingId);
    }

    public void sendBookingExpiredEvent(Long bookingId) {
        log.info("Producing booking expired event for booking ID: {}", bookingId);
        kafkaTemplate.send(TOPIC_BOOKING_EXPIRED, bookingId.toString(), bookingId);
    }
}