package pl.smyk.bookingservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.smyk.common.dto.BookingEventDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingEventProducer {

    private static final String TOPIC_BOOKING_CREATED = "booking_created";
    private static final String TOPIC_BOOKING_CONFIRMED = "booking_confirmed";
    private static final String TOPIC_BOOKING_CANCELLED = "booking_cancelled";
    private static final String TOPIC_BOOKING_EXPIRED = "booking_expired";

    private final KafkaTemplate<String, Object> kafkaTemplate;

//    public void sendBookingCreatedEvent(BookingDetailsResponse booking) {
//        log.info("Producing booking created event for booking ID: {}", booking.getId());
//        kafkaTemplate.send(TOPIC_BOOKING_CREATED, booking.getId().toString(), booking);
//    }

    public void sendBookingCreatedEvent(BookingEventDto booking) {
        log.info("Producing booking created event for booking ID: {}", booking.getBookingId());
        kafkaTemplate.send(TOPIC_BOOKING_CREATED, booking.getBookingId().toString(), booking);
    }

    public void sendBookingConfirmedEvent(BookingEventDto eventDto) {
        log.info("Producing booking confirmed event for booking ID: {}", eventDto.getBookingId());
        kafkaTemplate.send(TOPIC_BOOKING_CONFIRMED, eventDto.getBookingId().toString(), eventDto);
    }

    public void sendBookingCancelledEvent(BookingEventDto eventDto) {
        log.info("Producing booking cancelled event for booking ID: {}", eventDto.getBookingId());
        kafkaTemplate.send(TOPIC_BOOKING_CANCELLED, eventDto.getBookingId().toString(), eventDto);
    }

    public void sendBookingExpiredEvent(BookingEventDto eventDto) {
        log.info("Producing booking expired event for booking ID: {}", eventDto.getBookingId());
        kafkaTemplate.send(TOPIC_BOOKING_EXPIRED, eventDto.getBookingId().toString(), eventDto);
    }
}