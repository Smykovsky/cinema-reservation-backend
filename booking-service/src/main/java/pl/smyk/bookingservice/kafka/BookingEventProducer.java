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
    private static final String TOPIC_BOOKING_CANCELLED = "booking_cancelled";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendBookingCreatedEvent(BookingDetailsResponse booking) {
        log.info("Producing booking created event for booking ID: {}", booking.getId());
        kafkaTemplate.send(TOPIC_BOOKING_CREATED, booking.getId().toString(), booking);
    }

    public void sendBookingCancelledEvent(Long bookingId) {
        log.info("Producing booking cancelled event for booking ID: {}", bookingId);
        kafkaTemplate.send(TOPIC_BOOKING_CANCELLED, bookingId.toString(), bookingId);
    }
}